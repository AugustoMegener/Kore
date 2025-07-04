package com.cosmic_jewelry.common.core.preset

open class Preset<V, T>(val parents: Array<out Preset<V, T>>) {

    open fun T.action(value: V) {}

    private fun runAction(base: T, value: V) {
        parents.forEach { it.runAction(base, value) }
        base.action(value)
    }

    fun of(value: V, additional: (T.(V) -> Unit)? = null): T.() -> Unit = {
        runAction(this, value)
        additional?.invoke(this, value)
    }

    companion object {
        fun <V, T> allOf(value: V, vararg presets: Preset<V, T>, additional: (T.(V) -> Unit)? = null) =
            Preset(presets).of(value, additional)
    }
}