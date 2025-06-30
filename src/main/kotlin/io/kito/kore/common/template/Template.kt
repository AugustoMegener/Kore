package io.kito.kore.common.template

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.util.Indexable

/**
 * A generic interface for defining templates that manage a collection of items indexed by a specific type.
 * This interface extends [Indexable] to provide indexed access to the managed items.
 *
 * @param I The type of the index used to identify individual items within the template.
 * @param T The type of the items managed by this template.
 */
interface Template<I, T> : Indexable<I, T?> {

    val registry: EarlyRegistry<I>

    val registereds: Collection<() -> T> get() = registry.all.map { { get(it)!! } }

    operator fun contains(idx: I) = idx in registry.all

    abstract fun register()
}

