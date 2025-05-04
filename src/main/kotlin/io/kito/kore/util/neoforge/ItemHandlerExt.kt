package io.kito.kore.util.neoforge

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.ItemStackHandler

object ItemHandlerExt {
    operator fun IItemHandlerModifiable.set(idx: Int, value: ItemStack) { setStackInSlot(idx, value) }
    operator fun IItemHandler.get(idx: Int): ItemStack = getStackInSlot(idx)


    fun stackHandlerOf(vararg stack: ItemStack) = ItemStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))
    fun stackHandlerOf(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        ItemStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it[i] = s } }
}