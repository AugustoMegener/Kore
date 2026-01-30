package io.kito.kore.client.gui.kanvas.obj

import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation

class Texture private constructor(parent: KvsNode,
                                  pos: KvsVec,
                                  scale: KvsVec,
                                  val textureLocation: ResourceLocation)
    : Box(parent, pos, scale)
{
    var textureWidth: Int = width
    var textureHeight: Int = height

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED, textureLocation, pX + x, pY + y, 0f, 0f,
            textureWidth, textureHeight, width, height
        )
    }

    companion object {
        fun KvsNode.texture(texture: ResourceLocation, pos: KvsVec, scale: KvsVec) =
            Texture(this, pos, scale, texture).also { addChild(it) }

        fun KvsNode.texture(texture: ResourceLocation, transform: KvsTransform) =
            texture(texture, transform.pos, transform.scale)
    }
}