package io.kito.kore.common.registry

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGoup
import io.kito.kore.common.template.Template
import net.minecraft.resources.ResourceLocation
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

    override val indexesIds = arrayListOf<ResourceLocation>()
    val groups = arrayListOf<EarlyRegistryGoup>()

    override fun putAllIndexes() { indexesIds += registry.idxs }

    override fun putIndex(id: ResourceLocation) { indexesIds += id }

    override fun putIndex(group: EarlyRegistryGoup) { groups += group }

    override fun register() {
        indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
        indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

        indexes.forEach { registeredEntries[it] = builder(it) }
    }

    override fun get(idx: I) = registeredEntries[idx]?.get()
}
