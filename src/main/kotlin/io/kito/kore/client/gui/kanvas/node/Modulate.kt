package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.theme.colors.KvsColor
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import net.minecraft.client.gui.GuiGraphics

class Modulate(override val parent: KvsNode, override var pos: KvsVec, val color: KvsColor) : KvsNodeBase() {

    override var scale: KvsVec = KvsVec.FitChildren

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        val pose = guiGraphics.pose()
    }


}