package io.kito.kore.client.gui.kanvas.theme.colors

import io.kito.kore.client.gui.kanvas.node.KvsNode

@JvmInline
value class KvsColor(private val value: KvsNode.() -> Int) {

    companion object {

        fun KvsNode.hexOf(color: KvsColor) = color.value(this)
    }
}