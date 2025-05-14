package io.kito.kore.common.world.item.crafting

import io.kito.kore.util.neoforge.ItemHandlerExt.get
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.items.IItemHandler

class ItemHandlerRecipeInput<T: IItemHandler>(val handler: T) : IItemHandler by handler, RecipeInput {
    override fun getItem(index: Int) = get(index)

    override fun size() = slots
}