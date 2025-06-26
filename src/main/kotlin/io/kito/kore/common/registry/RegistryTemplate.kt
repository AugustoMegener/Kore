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
    // This map will store the *actually* registered DeferredHolder instances, after the call to register()
    private val registeredEntries = hashMapOf<I, DeferredHolder<*, T>>()

    // This list will store the index suppliers passed to addEntry
    private val pendingEntrySuppliers = mutableListOf<() -> I>()

    // allIdxs should reflect the indices of items that have been effectively registered
    override val allIdxs by lazy { registeredEntries.keys }

    /**
     * Adds the index suppliers to the pending list.
     * The invocation of suppliers and the actual registration will occur in register().
     * @param idxs A vararg of suppliers for the indices.
     */
    override fun addEntry(vararg idxs: () -> I) {
        pendingEntrySuppliers.addAll(idxs)
    }

    /**
     * Performs the registration of items.
     * Invokes all index suppliers added via addEntry
     * and registers them in the entries map.
     */
    override fun register() {
        pendingEntrySuppliers.forEach { supplier ->
            val idx = supplier() // HERE is where the supplier is invoked!
            registeredEntries[idx] = builder(idx)
        }
        pendingEntrySuppliers.clear() // Clears the list of pending suppliers after registration
    }

    /**
     * Retrieves a registered item by its index.
     * @param idx The index of the item.
     * @return The registered item instance, or `null` if not found.
     */
    override fun get(idx: I) = registeredEntries[idx]?.get()
}
