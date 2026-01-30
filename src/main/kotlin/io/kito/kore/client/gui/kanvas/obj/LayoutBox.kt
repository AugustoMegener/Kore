package io.kito.kore.client.gui.kanvas.obj

import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Function.Companion.fn
import io.kito.kore.util.minecraft.minecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ScrollableLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

open class LayoutBox(val layout: Layout, override val parent: KvsNode, override var pos: KvsVec) : KvsNodeBase() {

    override var scale: KvsVec = fn({ layout.width }, { layout.height })

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        guiGraphics.enableScissor(pX, pY, pX + width, pY + height)

        layout.arrangeElements()
        layout.visitWidgets {
            it.render(guiGraphics, pX + x + layout.x, pY + y + layout.y, partialTick)
        }
        layout.visitChildren { child ->
            child.visitWidgets {
                it.render(guiGraphics, pX + x + layout.x + child.x, pY + y + layout.y + child.y, partialTick)
            }
        }

        guiGraphics.disableScissor()
    }

    companion object {

        fun KvsNode.layout(layout: Layout, pos: KvsVec) =
            LayoutBox(layout, this, pos).also { addChild(it) }

        fun KvsNode.scroll(pos: KvsVec, node: KvsNode) =
            layout(ScrollableLayout(minecraftClient, KvsNodeLayout(node), node.height), pos)

        fun KvsNode.scroll(pos: KvsVec, label: Component, node: KvsNode) =
            layout(ScrollableLayout(minecraftClient, KvsNodeLayout(node, label), node.height), pos)
    }
}