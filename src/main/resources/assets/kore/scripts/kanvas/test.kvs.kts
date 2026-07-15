@file:Suppress("UnusedLambdaExpressionBody")

package assets.kore.scripts.kanvas

import io.kito.kore.client.gui.kanvas.kvs
import io.kito.kore.client.gui.kanvas.lorem
import io.kito.kore.client.gui.kanvas.node.Box.Companion.box
import io.kito.kore.client.gui.kanvas.node.Box.Companion.marginBox
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.plus
import io.kito.kore.client.gui.kanvas.node.Text.Companion.string
import io.kito.kore.client.gui.kanvas.node.Text.Companion.text
import io.kito.kore.client.gui.kanvas.theme.Theme.guiBackground
import io.kito.kore.client.gui.kanvas.theme.Theme.guiForeground
import io.kito.kore.client.gui.kanvas.theme.Theme.koreLogo
import io.kito.kore.client.gui.kanvas.theme.Theme.textArea
import io.kito.kore.client.gui.kanvas.theme.Theme.textFrame
import io.kito.kore.client.gui.kanvas.theme.colors.Colors.errorTextColor
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

kvs {
    


    guiBackground(Centered, xy(px(300), pt(0.75f))) + {
        box(start, xy(fill, px(48))) + {
            guiForeground(margin(px(5))) + {
                marginBox(px(3)) + {
                    koreLogo(start, px(32))
                    text("Kore".literal.toBold(), xy(AfterLast, Centered), xy(cw(5), lh(1)))
                        .color(errorTextColor)
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
