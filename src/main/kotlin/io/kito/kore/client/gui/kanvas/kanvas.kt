package io.kito.kore.client.gui.kanvas

import io.kito.kore.client.gui.kanvas.theme.colors.Colors.red
import io.kito.kore.client.gui.kanvas.theme.colors.Colors.rgb
import io.kito.kore.client.gui.kanvas.theme.Theme.guiBackground
import io.kito.kore.client.gui.kanvas.theme.Theme.koreLogo
import io.kito.kore.client.gui.kanvas.theme.Theme.guiForeground
import io.kito.kore.client.gui.kanvas.theme.Theme.textArea
import io.kito.kore.client.gui.kanvas.theme.Theme.textFrame
import io.kito.kore.client.gui.kanvas.node.Box.Companion.box
import io.kito.kore.client.gui.kanvas.node.Box.Companion.marginBox
import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.plus
import io.kito.kore.client.gui.kanvas.node.Text.Companion.string
import io.kito.kore.client.gui.kanvas.node.Text.Companion.text
import io.kito.kore.client.gui.kanvas.node.root.FlexRoot
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

typealias KvsBuilder<T> = KvsNode.(T) -> Unit

val lorem = """
    Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.
""".trimIndent()

val errorScreen: (ResourceLocation, ResultWithDiagnostics.Failure) -> KvsBuilder<Any> = { loc, error ->
    {
        guiBackground(Centered, xy(pt(0.85f), pt(0.75f))) + {
            box(start, xy(fill, px(48))) + {
                guiForeground(margin(px(5))) + {
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

fun kvs(block: KvsNode.() -> Unit): KvsBuilder<Unit> = { block() }
fun <T> kvs(block: KvsNode.(T) -> Unit): KvsBuilder<T> = { block(it) }

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

tailrec fun KvsNode.resolveAbsolutePosition(acc: Vector2i = Vector2i()): Vector2i =
    parent.resolveAbsolutePosition(Vector2i(acc.x + this.pos.x(this), acc.y + this.pos.y(this)))


fun KvsNode.renderTree(gui: GuiGraphics, partialTick: Float) {
    forEachScreenObject { x, y, obj -> obj.render(gui, x, y, partialTick) }
}

fun guiRoot(gui: GuiGraphics, builder: KvsBuilder<Unit>) =
    FlexRoot(gui::guiWidth, gui::guiHeight).apply { builder(Unit) }

fun <T> guiRoot(gui: GuiGraphics, builder: KvsBuilder<T>, ctx: T) =
    FlexRoot(gui::guiWidth, gui::guiHeight).apply { builder(ctx) }