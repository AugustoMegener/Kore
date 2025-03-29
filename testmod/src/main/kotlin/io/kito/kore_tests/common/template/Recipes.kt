package io.kito.kore_tests.common.template

import io.kito.kore.common.template.ActionTemplate
import io.kito.kore.util.minecraft.shaped
import io.kito.kore_tests.DataGenerator.recipe
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.Blocks
import io.kito.kore_tests.common.registry.Blocks.blockTemplate
import io.kito.kore_tests.common.registry.Items
import io.kito.kore_tests.common.registry.Items.itemTemplate

import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe


object Recipes {
    val recipeTemplate = ActionTemplate { i: String ->
        recipe(local("${i}_recipe")) {
            ShapedRecipe(ID, CraftingBookCategory.MISC,
                shaped("###",
                       "###",
                       "###")
                    .by('#' to Ingredient.of(itemTemplate[i]!!)),
                blockTemplate.item[i]!!.defaultInstance
            )
        }
    }
}