package io.kito.kore.common.world.item.crafting

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

abstract class KRecipe : Recipe<RecipeInput> {
    override fun matches(
        input: RecipeInput,
        level: Level
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun assemble(
        input: RecipeInput,
        registries: HolderLookup.Provider
    ): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }

    override fun getType(): RecipeType<*> {
        TODO("Not yet implemented")
    }
}