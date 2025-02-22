package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class DynamicItemModelProvider(output              : PackOutput,
                               modid               : String,
                               existingFileHelper  : ExistingFileHelper,
                               private val entries : List<(ItemModelProvider) -> Unit>)
    : ItemModelProvider(output, modid, existingFileHelper)
{
    override fun registerModels() { entries.forEach(::apply) }
}