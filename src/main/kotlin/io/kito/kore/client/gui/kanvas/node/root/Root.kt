package io.kito.kore.client.gui.kanvas.node.root

import io.kito.kore.client.gui.kanvas.KvsBuilder
import io.kito.kore.client.gui.kanvas.node.KvsNodeBase
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.start
import net.minecraft.client.gui.GuiGraphics

abstract class Root : KvsNodeBase() {

    override val parent = this
    override var pos: KvsVec = start

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {}
}