package io.kito.kore.common.world.item.crafting

import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.Recipe

class KRecipeBuilder<R: KRecipe<*>>(val recipe: R) : KRecipeBuilderBase<R, KRecipeBuilder<R>>() {
    override fun createRecipe() = recipe

    override fun save(recipeOutput: RecipeOutput, resourceKey: ResourceKey<Recipe<*>>) {
        recipeOutput.accept(resourceKey, recipe, null)
    }

    companion object {
        val <R: KRecipe<*>> R.builder get() = KRecipeBuilder(this)
    }
}