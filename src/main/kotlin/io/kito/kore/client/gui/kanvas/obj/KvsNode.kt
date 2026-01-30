package io.kito.kore.client.gui.kanvas.obj

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

        val KvsNode.width  get() = scale.x(this)
        val KvsNode.height get() = scale.y(this)

        operator fun KvsNode.plus(block: KvsNode.() -> Unit) = block()
    }
}