package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.data.LanguageProvider
import java.util.concurrent.CompletableFuture

class DynamicRecipeProvider(output: PackOutput,
                            registries: CompletableFuture<HolderLookup.Provider>,
                            private val entries : List<(RecipeOutput, HolderLookup.Provider) -> Unit>) :
    RecipeProvider(output, registries)
{
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        entries.forEach { it(recipeOutput, holderLookup) }
    }
}