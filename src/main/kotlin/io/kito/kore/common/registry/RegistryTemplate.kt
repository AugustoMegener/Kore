package io.kito.kore.common.registry

import io.kito.kore.common.registry.early.EarlyRegistry
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
class RegistryTemplate<I, T>(override val registry: EarlyRegistry<I>,val builder: (I) -> DeferredHolder<*, T>)
    : Template<I, T>
{
    private val registeredEntries = hashMapOf<I, DeferredHolder<*, T>>()

    /**
     * Performs the registration of items.
     * Invokes all index suppliers added via addEntry
     * and registers them in the entries map.
     */
    override fun register() {
        registry.all.forEach { registeredEntries[it] = builder(it) }
    }

    override fun get(idx: I) = registeredEntries[idx]?.get()
}
