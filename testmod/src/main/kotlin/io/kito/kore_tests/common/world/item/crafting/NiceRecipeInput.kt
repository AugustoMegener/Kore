package io.kito.kore_tests.common.world.item.crafting

import io.kito.kore.common.world.item.crafting.ItemHandlerRecipeInput
import net.neoforged.neoforge.items.IItemHandler

class NiceRecipeInput(handler: IItemHandler, input1Slot: Int, input2Slot: Int) :
    ItemHandlerRecipeInput<IItemHandler>(handler)
{
    override val indexes = mapOf(0 to input1Slot,
                                 1 to input2Slot)
}