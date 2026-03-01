package io.kito.kore.client.gui.kanvas.theme

import io.kito.kore.common.config.KoreConfigClient
import io.kito.kore.util.minecraft.ResourceLocationExt.on
import io.kito.kore.util.minecraft.ResourceLocationExt.plus
import net.minecraft.resources.ResourceLocation

interface ThemeProvider {
    val themeLocation: ResourceLocation
        get() = KoreConfigClient.kanvasTheme

    companion object {
        fun ThemeProvider.themeLoc(path: String) = "themes/" on themeLocation + "/$path"
    }
}