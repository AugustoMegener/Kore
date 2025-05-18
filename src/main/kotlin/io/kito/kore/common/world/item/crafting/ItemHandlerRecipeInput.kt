package io.kito.kore.common.world.item.crafting

import io.kito.kore.util.neoforge.ItemHandlerExt.get
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.items.IItemHandler

abstract class ItemHandlerRecipeInput<T: IItemHandler>(val handler: T) : IItemHandler, RecipeInput {

    abstract val indexes: Map<Int, Int>

    override fun getItem(index: Int) = get(indexes[index] ?: throw IllegalStateException("Invalid index: $index"))

    override fun size() = indexes.keys.max()


    override fun getStackInSlot(slot: Int) =
        handler[indexes[slot] ?: throw IllegalStateException("Invalid index: $slot")]

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
        handler.insertItem(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean) =
        handler.extractItem(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), amount, simulate)

    override fun getSlots() = size()

    override fun getSlotLimit(slot: Int) =
        handler.getSlotLimit(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"))

    override fun isItemValid(slot: Int, stack: ItemStack) =
        handler.isItemValid(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), stack)
}