package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class DynamicBlockStateProvider(output              : PackOutput,
                                modid               : String,
                                existingFileHelper  : ExistingFileHelper,
                                private val entries : List<(DynamicBlockStateProvider) -> Unit>)
    : BlockStateProvider(output, modid, existingFileHelper)
{
    override fun registerStatesAndModels() { entries.forEach(::apply) }
}