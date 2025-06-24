package io.kito.kore.util.neoforge

import io.kito.kore.util.neoforge.ItemHandlerExt.set
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.ItemStackHandler
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

    /**
     * Creates a new [ItemStackHandler] for a [BlockEntity] that automatically calls `setChanged()` when contents change.
     * @return A new [ItemStackHandler] instance.
     */
    fun BlockEntity.beStackHandler() =
        object : ItemStackHandler() { override fun onContentsChanged(slot: Int) { setChanged() } }

    /**
     * Creates a new [ItemStackHandler] with a specified size for a [BlockEntity] that automatically calls `setChanged()` when contents change.
     * @param size The number of slots in the item stack handler.
     * @return A new [ItemStackHandler] instance.
     */
    fun BlockEntity.beStackHandler(size: Int) =
        object : ItemStackHandler(size) { override fun onContentsChanged(slot: Int) { setChanged() } }

    /**
     * Creates a new [ItemStackHandler] with initial stacks for a [BlockEntity] that automatically calls `setChanged()` when contents change.
     * @param stacks A [NonNullList] of [ItemStack]s to initialize the handler with.
     * @return A new [ItemStackHandler] instance.
     */
    fun BlockEntity.beStackHandler(stacks: NonNullList<ItemStack>) =
        object : ItemStackHandler(stacks) { override fun onContentsChanged(slot: Int) { setChanged() } }

    /**
     * Creates a new [ItemStackHandler] with a vararg of initial stacks for a [BlockEntity] that automatically calls `setChanged()` when contents change.
     * @param stack A vararg of [ItemStack]s to initialize the handler with.
     * @return A new [ItemStackHandler] instance.
     */
    fun BlockEntity.beStackHandler(vararg stack: ItemStack) =
        beStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))

    /**
     * Creates a new [ItemStackHandler] with a specified size and initial stacks at specific indices for a [BlockEntity] that automatically calls `setChanged()` when contents change.
     * @param size The number of slots in the item stack handler.
     * @param stacks A vararg of [Pair]s, where each pair consists of an index and an [ItemStack] to set at that index.
     * @return A new [ItemStackHandler] instance.
     */
    fun BlockEntity.beStackHandler(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        beStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it[i] = s } }

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

