package io.kito.kore.client.gui.kanvas.node

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.y
import io.kito.kore.client.gui.kanvas.node.Text.OverflowType.*
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.util.minecraft.literal
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.translatable

class Text private constructor(parent: KvsNode,
                               pos: KvsVec,
                               scale: KvsVec,
                               val text: Component,
)
    : Box(parent, pos, scale)
{
    enum class OverflowType { WRAP_POP, WRAP_CUT, POP, CUT, SCROLL, }

    private var overflowType = WRAP_POP
    private var color = -1
    private var dropShadow = true

    override fun render(guiGraphics: GuiGraphics, pX: Int, pY: Int, partialTick: Float) {
        when(overflowType) {
            WRAP_POP    -> drawWordWrap(guiGraphics, pX + x, pY + y, false)
            WRAP_CUT    -> drawWordWrap(guiGraphics, pX + x, pY + y)
            SCROLL      -> guiGraphics.drawScrollingString(font, text, pX + x, pX + x + width, pY + y, color)
            POP         -> guiGraphics.drawString(font, text, pX + x, pY + y, color, dropShadow)
            CUT         -> guiGraphics.drawString(font, font.split(text, width)[0], pX + x, pY + y, color, dropShadow)

        }
    }

    fun wrapCuttingOverflow() = also { overflowType = WRAP_CUT }
    fun popOverflow() = also { overflowType = POP }
    fun hiddenOverflow() = also { overflowType = CUT }
    fun scrollingOverflow() = also { overflowType = SCROLL }

    fun color(hex: Int) = also { color = hex }

    fun noShadow() = also { dropShadow = false }

    private fun drawWordWrap(guiGraphics: GuiGraphics, x: Int, y: Int, respectHeight: Boolean = true) {
        var yPos = y
        for (line in font.split(text, width)) {
            guiGraphics.drawString(font, line, x, yPos, color, dropShadow)
            yPos += font.lineHeight
            if ((yPos - y) >= height && respectHeight) break
        }
    }

    companion object {

        fun KvsNode.text(text: Component, pos: KvsVec, scale: KvsVec) =
            Text(this, pos, scale, text).also { addChild(it) }

        fun KvsNode.text(text: Component, transform: KvsTransform) =
            text(text, transform.pos, transform.scale)

        fun KvsNode.string(text: String, pos: KvsVec, scale: KvsVec) =
            text(text.literal, pos, scale)

        fun KvsNode.string(text: String, transform: KvsTransform) =
            text(text.literal, transform.pos, transform.scale)

        fun KvsNode.translatable(text: String, pos: KvsVec, scale: KvsVec) =
            text(translatable(text), pos, scale)

        fun KvsNode.translatable(text: String, transform: KvsTransform) =
            text(translatable(text), transform.pos, transform.scale)
    }
}