package io.kito.kore.client.gui.kanvas

import io.kito.kore.client.gui.kanvas.Colors.red
import io.kito.kore.client.gui.kanvas.Colors.rgb
import io.kito.kore.client.gui.kanvas.Theme.guiBackground
import io.kito.kore.client.gui.kanvas.Theme.koreLogo
import io.kito.kore.client.gui.kanvas.Theme.secondaryGuiBackground
import io.kito.kore.client.gui.kanvas.Theme.textArea
import io.kito.kore.client.gui.kanvas.Theme.textFrame
import io.kito.kore.client.gui.kanvas.obj.Box.Companion.box
import io.kito.kore.client.gui.kanvas.obj.Box.Companion.marginBox
import io.kito.kore.client.gui.kanvas.obj.KanvasBuilder
import io.kito.kore.client.gui.kanvas.obj.KvsNode
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.plus
import io.kito.kore.client.gui.kanvas.obj.Root
import io.kito.kore.client.gui.kanvas.obj.Text.Companion.string
import io.kito.kore.client.gui.kanvas.obj.Text.Companion.text
import io.kito.kore.client.gui.kanvas.transform.KvsTransform.Companion.margin
import io.kito.kore.client.gui.kanvas.transform.KvsVec.*
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.cw
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.lh
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.px
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.start
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Compound.Companion.xy
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Relative.Companion.fill
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Relative.Companion.pt
import io.kito.kore.util.minecraft.literal
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2i
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.ERROR
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.FATAL

val lorem = """
    Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.
""".trimIndent()

val errorScreen: (ResourceLocation, ResultWithDiagnostics.Failure) -> KanvasBuilder<Any> = { loc, error ->
    {
        guiBackground(Centered, xy(pt(0.85f), pt(0.75f))) + {
            box(start, xy(fill, px(48))) + {
                secondaryGuiBackground(margin(px(5))) + {
                    marginBox(px(3)) + {
                        koreLogo(start, px(32))
                        box(xy(AfterLast, start), Expand) + {
                            text("Kore".literal.withStyle(Style.EMPTY.withBold(true)), start, xy(cw(4), lh(1)))
                                .color(rgb(146, 52, 235))
                            string("Unable to load $loc script!",
                                xy(start, lh(1)), xy(cw(50), lh(1)))
                                .color(red)
                        }
                    }
                }
            }
            box(xy(start, AfterLast), xy(fill, Expand)) + {
                textArea(margin(px(5))) + {
                    marginBox(px(5)) + {
                        string(error.reports
                            .filter { it.severity == FATAL || it.severity == ERROR }
                            .joinToString("\n") { it.render() },
                            start, fill
                        ).color(red)
                    }
                }
                textFrame(margin(px(5)))
            }
        }
    }
}

tailrec fun resolveAbsolutePositions(pending: List<Pair<Vector2i, KvsNode>>,
                                     acc: List<Pair<Vector2i, KvsNode>> = emptyList()):
        List<Pair<Vector2i, KvsNode>>
{
    if (pending.isEmpty()) return acc
    else pending.first().let { (pos, obj) -> return resolveAbsolutePositions(
        obj.children.map { Vector2i(pos.x + obj.pos.x(obj), pos.y + obj.pos.y(obj)) to it } + pending.drop(1),
        acc + (pos to obj)
    ) }
}


fun KvsNode.resolveAbsolutePositions() = resolveAbsolutePositions(listOf(Vector2i() to this))

inline fun KvsNode.forEachScreenObject(block: (x: Int, y: Int, KvsNode) -> Unit) {
    resolveAbsolutePositions().forEach { (v, o) -> block(v.x, v.y, o) }
}

fun KvsNode.renderTree(gui: GuiGraphics, partialTick: Float) {
    forEachScreenObject { x, y, obj -> obj.render(gui, x, y, partialTick) }
}

fun guiRoot(gui: GuiGraphics, builder: KanvasBuilder<Unit>) =
    Root(gui::guiWidth, gui::guiHeight).apply { builder(this, Unit) }

fun <T> guiRoot(gui: GuiGraphics, builder: KanvasBuilder<T>, ctx: T) =
    Root(gui::guiWidth, gui::guiHeight).apply { builder(this, ctx) }