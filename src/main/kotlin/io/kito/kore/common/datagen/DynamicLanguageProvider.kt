package io.kito.kore.common.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class DynamicLanguageProvider(output              : PackOutput,
                              modid               : String,
                              locale              : String,
                              private val entries : List<(LanguageProvider) -> Unit>)
    : LanguageProvider(output, modid, locale)
{
    override fun addTranslations() { entries.forEach(::apply) }
}