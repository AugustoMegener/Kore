package io.kito.kore_tests.common.world.item.crafting

import io.kito.kore.common.data.Save
import io.kito.kore.common.datagen.DataGen
import io.kito.kore.common.world.item.crafting.*
import io.kito.kore_tests.DataGenerator
import io.kito.kore_tests.DataGenerator.recipe
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.FluidTypes.fluidTemplate
import io.kito.kore_tests.common.registry.Items.itemTemplate
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.common.crafting.SizedIngredient

data class NiceRecipe(@Save @Slot(0) val input1: Ingredient,
                      @Save @Slot(1) val input2: SizedIngredient,
                      @Save @Result  val result: ItemStack) :
    KRecipeItemHandler()
{
    @RegisterRecipeType
    companion object : KRecipeType<NiceRecipe>(local("my_nice_recipe"), NiceRecipe::class) {

        @DataGen(DataGenerator::class)
        fun registerRecipes() {

            recipe(local("my_nice_recipe")) {
                NiceRecipe(
                    Ingredient.of(itemTemplate["nice"]),
                    SizedIngredient.of(itemTemplate["weird"]!!, 5),
                    ItemStack(fluidTemplate.flowingFluid.bucketItem["fool"]!!)
                )
            }
        }
    }
}
