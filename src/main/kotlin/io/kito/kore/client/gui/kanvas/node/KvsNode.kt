package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.root.Root
import io.kito.kore.client.gui.kanvas.theme.ThemeProvider
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.util.minecraft.minecraftClient
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics

interface KvsNode {

    var pos : KvsVec
    var scale : KvsVec

    val parent: KvsNode
    val children: List<KvsNode>

    val font: Font get() = minecraftClient.font

    fun addChild(obj: KvsNode)

    fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float)

    companion object {

        val KvsNode.x get() = pos.x(this)
        val KvsNode.y get() = pos.y(this)

        val KvsNode.index get() = parent.children.indexOf(this)

        val KvsNode.width  get() = scale.x(this)
        val KvsNode.height get() = scale.y(this)

        tailrec fun KvsNode.root(): Root = (this as? Root) ?: parent.root()
        tailrec fun KvsNode.theme(): ThemeProvider = (this as? ThemeProvider) ?: parent.theme()

        operator fun KvsNode.plus(block: KvsNode.() -> Unit) = block()
    }
}