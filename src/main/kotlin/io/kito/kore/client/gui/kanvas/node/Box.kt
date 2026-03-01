package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsTransform.Companion.margin
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics

open class Box protected constructor(override val parent: KvsNode,
                                     override var pos: KvsVec,
                                     override var scale: KvsVec) : KvsNodeBase()
{
    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {}

    companion object {
        fun KvsNode.box(pos: KvsVec, scale: KvsVec) =
            Box(this, pos, scale).also { addChild(it) }

        fun KvsNode.box(transform: KvsTransform) =
            box(transform.pos, transform.scale)

        fun KvsNode.marginBox(marginSize: KvsVec) =
            box(margin(marginSize))
    }
}