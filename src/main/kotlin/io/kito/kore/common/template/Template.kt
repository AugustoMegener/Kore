package io.kito.kore.common.template

import io.kito.kore.util.Indexable

/**
 * A generic interface for defining templates that manage a collection of items indexed by a specific type.
 * This interface extends [Indexable] to provide indexed access to the managed items.
 *
 * @param I The type of the index used to identify individual items within the template.
 * @param T The type of the items managed by this template.
 */
interface Template<I, T> : Indexable<I, T?> {

    /**
     * Returns a collection of all indices managed by this template.
     */
    val allIdxs: Collection<I>
    /**
     * Returns a collection of suppliers for all registered items.
     */
    val registereds: Collection<() -> T> get() = allIdxs.map { { get(it)!! } }

    /**
     * Checks if the template contains an item for the given index.
     * @param idx The index to check.
     * @return `true` if an item exists for the index, `false` otherwise.
     */
    operator fun contains(idx: I) = idx in allIdxs

    /**
     * Registers items for the given indices.
     * @param idxs A vararg of indices to addEntry.
     */
    fun addEntry(vararg idxs: () -> I)

    fun register()

    /**
     * Companion object providing utility functions for [Template]s.
     */
    companion object {
        /**
         * Extension function to addEntry items for a template.
         * @param I The type of the index.
         * @param T The type of the item.
         * @param R The type of the [Template].
         * @param idxs A vararg of indices to addEntry.
         * @return The [Template] instance for fluent chaining.
         */
        fun <I, T, R : Template<I, T>> R.on(vararg idxs: () -> I): R = also { addEntry(*idxs) }
    }
}

