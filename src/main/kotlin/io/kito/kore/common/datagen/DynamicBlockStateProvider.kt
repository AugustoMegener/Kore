package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

/**
 * A dynamic [BlockStateProvider] that allows for registering block states and models
 * using a list of lambda functions. This provides flexibility in defining block states
 * programmatically during data generation.
 *
 * @param output The [PackOutput] for writing generated data.
 * @param modid The mod ID for which data is being generated.
 * @param existingFileHelper An [ExistingFileHelper] to check for existing files.
 * @param entries A list of lambda functions, each taking a [DynamicBlockStateProvider] instance
 *                and applying block state and model definitions to it. These lambdas encapsulate
 *                the logic for generating specific block states.
 */
class DynamicBlockStateProvider(output              : PackOutput,
                                modid               : String,
                                existingFileHelper  : ExistingFileHelper,
                                private val entries : List<(DynamicBlockStateProvider) -> Unit>)
    : BlockStateProvider(output, modid, existingFileHelper)
{
    /**
     * Registers all block states and models by iterating through the provided [entries]
     * and applying each lambda function to this provider instance.
     */
    override fun registerStatesAndModels() { entries.forEach(::apply) }
}

