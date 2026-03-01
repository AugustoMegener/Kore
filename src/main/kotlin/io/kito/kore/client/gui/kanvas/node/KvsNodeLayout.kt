package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.px
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Compound.Companion.xy
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import java.util.function.Consumer

data class KvsNodeLayout(val node: KvsNode, val label: Component = Component.literal("")) : Layout {

    val widget = object : AbstractWidget(0, 0, width, height, label) {

        override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float)
            { node.render(guiGraphics, x, y, partialTick) }

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
    }

    override fun visitChildren(visitor: Consumer<LayoutElement>) {}

    override fun setX(x: Int) {
        node.pos = xy(px(x), node.pos)
    }

    override fun setY(y: Int) {
        node.pos = xy(node.pos, px(y))
    }

    override fun getX() = node.x

    override fun getY() = node.y

    override fun getWidth() = node.width

    override fun getHeight() = node.height

    override fun visitWidgets(consumer: Consumer<AbstractWidget>) { consumer.accept(widget) }
}