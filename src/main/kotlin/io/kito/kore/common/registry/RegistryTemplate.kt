package io.kito.kore.common.registry

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGroup
import io.kito.kore.common.template.Template
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.DeferredHolder

class RegistryTemplate<I, T>(override val registry: EarlyRegistry<I>,val builder: (I) -> DeferredHolder<*, T>)
    : Template<I, T>
{
    private val registeredEntries = hashMapOf<I, DeferredHolder<*, T>>()

    override val indexesIds = arrayListOf<ResourceLocation>()
    val groups = arrayListOf<EarlyRegistryGroup>()

    override fun putAllIndexes() { indexesIds += registry.idxs }

    override fun putIndex(id: ResourceLocation) { indexesIds += id }

    override fun putIndex(group: EarlyRegistryGroup) { groups += group }

    override fun register() {
        indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
        indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

        indexes.forEach { registeredEntries[it] = builder(it) }
    }

    override fun get(idx: I) = registeredEntries[idx]?.get()
}
