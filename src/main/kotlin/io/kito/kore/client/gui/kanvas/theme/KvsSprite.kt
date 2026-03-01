package io.kito.kore.client.gui.kanvas.theme

import io.kito.kore.client.gui.kanvas.theme.ThemeProvider.Companion.themeLoc
import net.minecraft.resources.ResourceLocation

sealed interface KvsSprite {

    fun locationFor(theme: ThemeProvider) : ResourceLocation

    @ConsistentCopyVisibility
    data class Static internal constructor(val location: ResourceLocation) : KvsSprite {
        override fun locationFor(theme: ThemeProvider) = location
    }

    @ConsistentCopyVisibility
    data class Themed internal constructor(private val path: String) : KvsSprite {
        override fun locationFor(theme: ThemeProvider) = theme.themeLoc(path)
    }

    companion object {

        fun ResourceLocation.staticSprite() = Static(this)
        fun kvsSprite(path: String) = Themed(path)
    }
}