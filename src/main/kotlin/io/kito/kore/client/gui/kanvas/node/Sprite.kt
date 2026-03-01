package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.theme
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.theme.KvsSprite
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation

open class Sprite private constructor(parent: KvsNode, pos: KvsVec, scale: KvsVec, val sprite: KvsSprite) :
    Box(parent, pos, scale)
{
    private var xOffset = 0
    private var yOffset = 0

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED, sprite.locationFor(theme()),
            width, height,
            xOffset, yOffset,
            pX + x, pY + y,
            width, height
        )
    }

    fun offset(x: Int, y: Int) = also {
        xOffset = x
        yOffset = y
    }

    companion object {



        fun KvsNode.sprite(sprite: KvsSprite, pos: KvsVec, scale: KvsVec) =
            Sprite(this, pos, scale, sprite).also { addChild(it) }

        fun KvsNode.sprite(sprite: KvsSprite, transform: KvsTransform) =
            sprite(sprite, transform.pos, transform.scale)


    }
}