package io.kito.kore.util.neoforge

import io.kito.kore.util.neoforge.ItemHandlerExt.set
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.reflect.KProperty

object BlockEntityExt {
    inline val BlockEntity.beLvl get() = level!!

    fun BlockEntity.beStackHandler() =
        object : ItemStackHandler() { override fun onContentsChanged(slot: Int) { setChanged() } }

    fun BlockEntity.beStackHandler(size: Int) =
        object : ItemStackHandler(size) { override fun onContentsChanged(slot: Int) { setChanged() } }

    fun BlockEntity.beStackHandler(stacks: NonNullList<ItemStack>) =
        object : ItemStackHandler(stacks) { override fun onContentsChanged(slot: Int) { setChanged() } }

    fun BlockEntity.beStackHandler(vararg stack: ItemStack) =
        beStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))

    fun BlockEntity.beStackHandler(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        beStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it[i] = s } }

    class AutoDirt<T>(private var data: T) {
        operator fun getValue(be: BlockEntity, prop: KProperty<*>) = data
        operator fun setValue(be: BlockEntity, prop: KProperty<*>, new: T) { data = new; be.setChanged() }
    }
}