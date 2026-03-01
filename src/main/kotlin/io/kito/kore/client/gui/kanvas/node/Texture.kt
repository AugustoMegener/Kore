package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.theme
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.theme.KvsTexture
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines

class Texture private constructor(parent: KvsNode,
                                  pos: KvsVec,
                                  scale: KvsVec,
                                  val texture: KvsTexture)
    : Box(parent, pos, scale)
{
    var textureWidth: Int = width
    var textureHeight: Int = height

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED, texture.locationFor(theme()), pX + x, pY + y, 0f, 0f,
            textureWidth, textureHeight, width, height
        )
    }

    companion object {
        fun KvsNode.texture(texture: KvsTexture, pos: KvsVec, scale: KvsVec) =
            Texture(this, pos, scale, texture).also { addChild(it) }

        fun KvsNode.texture(texture: KvsTexture, transform: KvsTransform) =
            texture(texture, transform.pos, transform.scale)
    }
}