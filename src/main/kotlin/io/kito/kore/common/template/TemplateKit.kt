package io.kito.kore.common.template

import io.kito.kore.util.UNCHECKED_CAST

/**
 * A utility class for managing a collection of [Template]s, allowing them to be registered and accessed together.
 * This class implements the [Template] interface, treating the collection of templates as a single unit.
 *
 * @param I The type of the index used across all managed templates.
 * @property templates An array of [Template]s to be managed by this kit.
 */
class TemplateKit<I>(templates: Array<out Template<I, *>>) : Template<I, Array<*>> {

    @Suppress(UNCHECKED_CAST)
    private var templates = templates as Array<Template<I, *>>

    val entries = arrayListOf<() -> I>()

    /**
     * A list to store all registered indices for this template kit.
     */
    override val allIdxs = arrayListOf<I>()

    private var static = false

    /**
     * Registers indices with this template kit.
     * Note: This only adds indices to the kit's internal list; actual registration with individual templates happens when [apply] is called.
     * @param idxs A vararg of indices to addEntry.
     */
    override fun addEntry(vararg idxs: () -> I) { entries += idxs }

    /**
     * Retrieves an array of items from all managed templates for a given index.
     * @param idx The index to retrieve items for.
     * @return An [Array] containing the items retrieved from each template at the specified index.
     */
    override fun get(idx: I) = templates.map { it[idx] } .toTypedArray()

    /**
     * Marks this [TemplateKit] as static, preventing further inclusion of new templates.
     * @return This [TemplateKit] instance for fluent chaining.
     */
    fun static() = also { static = true }

    /**
     * Includes new templates into this kit.
     * This operation is only allowed if the kit is not marked as static.
     * @param news A vararg of new [Template]s to include.
     * @throws IllegalStateException if the template kit is static.
     */
    fun include(vararg news: Template<I, *>) {
        if (static) throw IllegalStateException("Can't put new templates on a static template kit")
        else templates += news
    }

    /**
     * Applies the registered indices to all managed templates.
     * This method iterates through all collected indices and calls the `addEntry` method on each individual template.
     */
    override fun register() {
        templates.forEach { it.addEntry(*entries.toTypedArray()); it.register() }
    }

    /**
     * Companion object providing utility functions for [TemplateKit]s.
     */
    companion object {
        /**
         * Creates a new [TemplateKit] from a given set of templates.
         * @param I The type of the index.
         * @param templates A vararg of [Template]s to include in the kit.
         * @return A new [TemplateKit] instance.
         */
        fun <I> kitOf(vararg templates: Template<I, *>) = TemplateKit(templates)
    }
}

