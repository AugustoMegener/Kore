package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics

typealias RenderAreaRenderer =
            (guiGraphics: GuiGraphics, pX: Int, pY: Int, width: Int, height: Int, partialTick: Float) -> Unit

class RenderArea private constructor(parent: KvsNode, pos: KvsVec, scale: KvsVec, val renderer: RenderAreaRenderer) :
    Box(parent, pos, scale)
{
    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        renderer(guiGraphics, pX, pY, width, height, partialTick)
    }

    companion object {

        fun KvsNode.renderArea(pos: KvsVec, scale: KvsVec, renderer: RenderAreaRenderer) =
            RenderArea(this, pos, scale, renderer).also { addChild(it) }

        fun KvsNode.renderArea(transform: KvsTransform, renderer: RenderAreaRenderer) =
            renderArea(transform.pos, transform.scale, renderer)
    }
}