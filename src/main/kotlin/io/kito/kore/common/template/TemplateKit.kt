package io.kito.kore.common.template

import io.kito.kore.util.UNCHECKED_CAST

class TemplateKit<I>(templates: Array<out Template<I, *>>) : Template<I, Array<*>> {

    @Suppress(UNCHECKED_CAST)
    private var templates = templates as Array<Template<I, *>>

    override val allIdxs = arrayListOf<I>()

    private var static = false

    override fun register(vararg idxs: I) { allIdxs += idxs }

    override fun get(idx: I) = templates.map { it[idx] } .toTypedArray()

    fun static() = also { static = true }

    fun include(vararg news: Template<I, *>) {
        if (static) throw IllegalStateException("Can't put new templates on a static template kit")
        else templates += news
    }

    @Suppress(UNCHECKED_CAST)
    fun apply() {
        templates.forEach { it.register(*allIdxs.toArray() as Array<out I>) }
    }

    companion object {
        fun <I> kitOf(vararg templates: Template<I, *>) = TemplateKit(templates)
    }
}