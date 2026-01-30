@file:Suppress("UnusedLambdaExpressionBody")

package data.kore.scripts.kanvas

import io.kito.kore.client.gui.kanvas.Colors.rgb
import io.kito.kore.client.gui.kanvas.Theme.guiBackground
import io.kito.kore.client.gui.kanvas.Theme.koreLogo
import io.kito.kore.client.gui.kanvas.Theme.secondaryGuiBackground
import io.kito.kore.client.gui.kanvas.Theme.textArea
import io.kito.kore.client.gui.kanvas.Theme.textFrame
import io.kito.kore.client.gui.kanvas.lorem
import io.kito.kore.client.gui.kanvas.obj.Box.Companion.box
import io.kito.kore.client.gui.kanvas.obj.Box.Companion.marginBox
import io.kito.kore.client.gui.kanvas.obj.KvsNode.Companion.plus
import io.kito.kore.client.gui.kanvas.obj.Root.Companion.root
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
import io.kito.kore.util.minecraft.toBold

root {
    guiBackground(Centered, xy(px(300), pt(0.75f))) + {
        box(start, xy(fill, px(48))) + {
            secondaryGuiBackground(margin(px(5))) + {
                marginBox(px(3)) + {
                    koreLogo(start, px(32))
                    text("Kore".literal.toBold(), xy(AfterLast, Centered), xy(cw(5), lh(1)))
                        .color(rgb(146, 52, 235))
                }
            }
        }
        box(xy(start, AfterLast), xy(fill, Expand)) + {
            textArea(margin(px(5))) + {
                string(lorem, margin(px(5)))
            }
            textFrame(margin(px(5)))
        }
    }
}