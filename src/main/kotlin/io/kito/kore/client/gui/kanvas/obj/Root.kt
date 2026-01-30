package io.kito.kore.client.gui.kanvas.obj

import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.start
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Function.Companion.fn
import net.minecraft.client.gui.GuiGraphics

typealias KanvasBuilder<T> = Root.(T) -> Unit

open class Root(val width: () -> Int, val height: () -> Int) : KvsNodeBase() {

    override val parent = this

    override var pos: KvsVec = start
    override var scale: KvsVec = fn({ width() }, { height() })

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {}

    companion object {

        fun root(block: Root.() -> Unit): KanvasBuilder<Unit> = { block() }
        fun <T> root(block: Root.(T) -> Unit): KanvasBuilder<T> = { block(it) }
    }
}