package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.data.LanguageProvider

/**
 * A dynamic [LanguageProvider] that allows for registering translations
 * using a list of lambda functions. This provides flexibility in defining language entries
 * programmatically during data generation.
 *
 * @param output The [PackOutput] for writing generated data.
 * @param modid The mod ID for which data is being generated.
 * @param locale The locale for which translations are being added (e.g., "en_us").
 * @param entries A list of lambda functions, each taking a [LanguageProvider] instance
 *                and applying translation definitions to it. These lambdas encapsulate
 *                the logic for adding specific language entries.
 */
class DynamicLanguageProvider(output              : PackOutput,
                              modid               : String,
                              locale              : String,
                              private val entries : List<(LanguageProvider) -> Unit>)
    : LanguageProvider(output, modid, locale)
{
    /**
     * Adds all translations by iterating through the provided [entries]
     * and applying each lambda function to this provider instance.
     */
    override fun addTranslations() { entries.forEach(::apply) }


}

