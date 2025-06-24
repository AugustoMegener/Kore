package io.kito.kore.common.registry

import io.kito.kore.common.template.Template
import net.neoforged.neoforge.registries.DeferredHolder

/**
 * A generic template class for registering multiple items of a specific type based on an index.
 * This allows for defining a common structure for a set of related registry entries.
 *
 * @param I The type of the index used to identify individual entries within the template.
 * @param T The base type of the registered items (e.g., [Item], [Block], [EntityType]).
 * @property builder A lambda that takes an index of type [I] and returns a [DeferredHolder] for the registered item.
 */
class RegistryTemplate<I, T>(val builder: (I) -> DeferredHolder<*, T>) : Template<I, T> {
    private val entries = hashMapOf<I, DeferredHolder<*, T>>()

    override val allIdxs by lazy { entries.keys }

    /**
     * Registers items for the given indices using the provided builder.
     * @param idxs A vararg of indices for which to register items.
     */
    override fun register(vararg idxs: I) { idxs.forEach { entries[it] = builder(it) } }

    /**
     * Retrieves a registered item by its index.
     * @param idx The index of the item.
     * @return The registered item instance, or `null` if not found.
     */
    override fun get(idx: I) = entries[idx]?.get()
}

