package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

/**
 * A dynamic [ItemModelProvider] that allows for registering item models
 * using a list of lambda functions. This provides flexibility in defining item models
 * programmatically during data generation.
 *
 * @param output The [PackOutput] for writing generated data.
 * @param modid The mod ID for which data is being generated.
 * @param existingFileHelper An [ExistingFileHelper] to check for existing files.
 * @param entries A list of lambda functions, each taking an [ItemModelProvider] instance
 *                and applying item model definitions to it. These lambdas encapsulate
 *                the logic for generating specific item models.
 */
class DynamicItemModelProvider(output              : PackOutput,
                               modid               : String,
                               existingFileHelper  : ExistingFileHelper,
                               private val entries : List<(ItemModelProvider) -> Unit>)
    : ItemModelProvider(output, modid, existingFileHelper)
{
    /**
     * Registers all item models by iterating through the provided [entries]
     * and applying each lambda function to this provider instance.
     */
    override fun registerModels() { entries.forEach(::apply) }
}

