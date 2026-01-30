package io.kito.kore.client.gui.kanvas

object Colors {
    val white = -1
    val black = 0x000000ff
    val red = rgb(255, 0, 0)

    fun rgb(r: Int, g: Int, b: Int, a: Int = 255) = (a shl 24) or (r shl 16) or (g shl 8) or b
}