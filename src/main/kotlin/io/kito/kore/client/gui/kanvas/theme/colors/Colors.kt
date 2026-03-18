package io.kito.kore.client.gui.kanvas.theme.colors

import io.kito.kore.client.gui.kanvas.KanvasException
import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.theme
import io.kito.kore.common.resource.ClrReloadListener.getColors
import kotlin.math.abs
import kotlin.reflect.KProperty

typealias ColorScheme = Map<String, (KvsNode) -> Int>

object Colors {

    val red1 by ColorToken()
    val purple1 by ColorToken()


    val lightTextColor by ColorToken()
    val errorTextColor by ColorToken()

    class ColorToken(val name: String? = null) {

        operator fun getValue(obj: Any, prop: KProperty<*>) = KvsColor {
            colors()[name ?: prop.name]?.invoke(this)
                ?: throw KanvasException(
                    IllegalStateException("No ${name ?: prop.name} color on ${theme().themeLocation} theme!")
                )
        }
    }

    fun rgb(r: Int, g: Int, b: Int, a: Int = 255) = (a shl 24) or (r shl 16) or (g shl 8) or b

    fun hsl(h: Float, s: Float, l: Float, a: Float = 1f): Int {
        val hue = (h % 360 + 360) % 360
        val saturation = s.coerceIn(0f, 1f)
        val lightness = l.coerceIn(0f, 1f)
        val alpha = a.coerceIn(0f, 1f)

        val c = (1 - abs(2 * lightness - 1)) * saturation
        val x = c * (1 - abs((hue / 60f) % 2 - 1))
        val m = lightness - c / 2

        val (r1, g1, b1) = when {
            hue < 60  -> Triple(c, x, 0f)
            hue < 120 -> Triple(x, c, 0f)
            hue < 180 -> Triple(0f, c, x)
            hue < 240 -> Triple(0f, x, c)
            hue < 300 -> Triple(x, 0f, c)
            else      -> Triple(c, 0f, x)
        }

        val r = ((r1 + m) * 255).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255).toInt().coerceIn(0, 255)
        val alphaInt = (alpha * 255).toInt().coerceIn(0, 255)

        return (alphaInt shl 24) or (r shl 16) or (g shl 8) or b
    }

    fun KvsNode.colors() = getColors(theme().themeLocation)

    class ColorsBuilder internal constructor() {
        internal var values: ColorScheme = mapOf()

        operator fun String.invoke(getter: (KvsNode) -> Int) { values += this to getter  }
        infix fun String.of(getter: (KvsNode) -> String) {
            values += this to { it.colors()[getter(it)]!!.invoke(it) }
        }
    }

    fun colors(builder: ColorsBuilder.() -> Unit): ColorScheme = ColorsBuilder().apply(builder).values
}