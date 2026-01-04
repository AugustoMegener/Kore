package io.kito.kore.util.neoforge

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import kotlin.reflect.KProperty

/**
 * Utility object providing extension functions and classes for NeoForge [BlockEntity]s.
 */
object BlockEntityExt {
    /**
     * Extension property to get the [Level] of a [BlockEntity].
     * Throws [IllegalStateException] if the level is null.
     */
    inline val BlockEntity.beLvl get() = level!!


    fun BlockEntity.beStackHandler(size: Int) =
        object : ItemStacksResourceHandler(size)
        { override fun onContentsChanged(index: Int, previousContents: ItemStack) { setChanged() } }


    fun BlockEntity.beStackHandler(stacks: NonNullList<ItemStack>) =
        object : ItemStacksResourceHandler(stacks)
        { override fun onContentsChanged(index: Int, previousContents: ItemStack) { setChanged() } }

    fun BlockEntity.beStackHandler(vararg stack: ItemStack) =
        beStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))

    fun BlockEntity.beStackHandler(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        beStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it.set(i, ItemResource.of(s), s.count)} }

    /**
     * A property delegate that automatically calls `setChanged()` on the [BlockEntity] when its value is modified.
     * This is useful for properties that need to trigger a save or update when changed.
     * @param T The type of the data being delegated.
     * @property data The actual data being held by the delegate.
     */
    class AutoDirt<T>(private var data: T) {
        /**
         * Gets the value of the delegated property.
         * @param be The [BlockEntity] instance.
         * @param prop The [KProperty] representing the delegated property.
         * @return The current value of the data.
         */
        operator fun getValue(be: BlockEntity, prop: KProperty<*>) = data
        /**
         * Sets the value of the delegated property and calls `setChanged()` on the [BlockEntity].
         * @param be The [BlockEntity] instance.
         * @param prop The [KProperty] representing the delegated property.
         * @param new The new value to set.
         */
        operator fun setValue(be: BlockEntity, prop: KProperty<*>, new: T) { data = new; be.setChanged() }
    }
}

