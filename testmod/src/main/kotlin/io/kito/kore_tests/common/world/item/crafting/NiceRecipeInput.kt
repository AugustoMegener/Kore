package io.kito.kore_tests.common.world.item.crafting

import io.kito.kore.common.world.item.crafting.ItemHandlerRecipeInput
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource

class NiceRecipeInput(handler: ResourceHandler<ItemResource>, input1Slot: Int, input2Slot: Int) :
    ItemHandlerRecipeInput<ResourceHandler<ItemResource>>(handler)
{
    override val indexes = mapOf(0 to input1Slot,
                                 1 to input2Slot)


}