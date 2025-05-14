package io.kito.kore.common.world.item.crafting

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation

abstract class KRecipeBuilderBase<R: KRecipe<*>, T : KRecipeBuilderBase<R, T>> : RecipeBuilder {

    private val criteria = LinkedHashMap<String, Criterion<*>>()
    private var group: String? = null

    private val recipe: R by lazy(::createRecipe)

    internal open val prefix = ""

    override fun unlockedBy(name: String, criterion: Criterion<*>): T {
        criteria[name] = criterion
        return this as T
    }

    override fun group(groupName: String?): T {
        group = groupName
        return this as T
    }

    override fun getResult() = recipe.itemResult

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val advancement = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach(advancement::addCriterion)

        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/$prefix")))
    }

    abstract fun createRecipe(): R
}