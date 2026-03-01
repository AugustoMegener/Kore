package io.kito.kore.client.gui.kanvas.theme

import io.kito.kore.client.gui.kanvas.theme.ThemeProvider.Companion.themeLoc
import io.kito.kore.util.minecraft.ResourceLocationExt.gui
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import net.minecraft.resources.ResourceLocation

sealed interface KvsTexture {

    fun locationFor(theme: ThemeProvider) : ResourceLocation

    @ConsistentCopyVisibility
    data class Static internal constructor(val location: ResourceLocation) : KvsTexture {
        override fun locationFor(theme: ThemeProvider) = location.gui.texture.png
    }

    @ConsistentCopyVisibility
    data class Themed internal constructor(private val path: String) : KvsTexture {
        override fun locationFor(theme: ThemeProvider) = theme.themeLoc(path).gui.texture.png
    }

    companion object {

        fun ResourceLocation.staticTexture() = Static(this)
        fun kvsTexture(path: String) = Themed(path)
    }
}