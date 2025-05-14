package io.kito.kore.common.world.item.crafting

class KRecipeBuilder<R: KRecipe<*>>(val recipe: R) : KRecipeBuilderBase<R, KRecipeBuilder<R>>() {
    override fun createRecipe() = recipe

    companion object {
        val <R: KRecipe<*>> R.builder get() = KRecipeBuilder(this)
    }
}