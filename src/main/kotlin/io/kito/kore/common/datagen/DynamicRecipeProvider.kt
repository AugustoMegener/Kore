package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.data.LanguageProvider
import java.util.concurrent.CompletableFuture

/**
 * A dynamic [RecipeProvider] that allows for registering recipes
 * using a list of lambda functions. This provides flexibility in defining recipes
 * programmatically during data generation.
 *
 * @param output The [PackOutput] for writing generated data.
 * @param registries A [CompletableFuture] that provides a [HolderLookup.Provider] for accessing registries.
 * @param entries A list of lambda functions, each taking a [RecipeOutput] and a [HolderLookup.Provider]
 *                and applying recipe definitions to it. These lambdas encapsulate
 *                the logic for generating specific recipes.
 */
class DynamicRecipeProvider(output: PackOutput,
                            registries: CompletableFuture<HolderLookup.Provider>,
                            private val entries : List<(RecipeOutput, HolderLookup.Provider) -> Unit>) :
    RecipeProvider(output, registries)
{
    /**
     * Builds all recipes by iterating through the provided [entries]
     * and applying each lambda function to the given [RecipeOutput] and [HolderLookup.Provider].
     *
     * @param recipeOutput The [RecipeOutput] to register recipes with.
     * @param holderLookup The [HolderLookup.Provider] for accessing registries.
     */
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        entries.forEach { it(recipeOutput, holderLookup) }
    }
}

