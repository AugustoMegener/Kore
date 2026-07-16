package io.kito.kore.common.template

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGroup
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

class TagTemplate<I, T>(override val registry: EarlyRegistry<I>, val supplier: (I) -> TagKey<T>) :
    Template<I, TagKey<T>>
{

    override val indexesIds = arrayListOf<ResourceLocation>()
    val groups = arrayListOf<EarlyRegistryGroup>()

    val tags = hashMapOf<I, TagKey<T>>()

    override fun putAllIndexes() { indexesIds += registry.idxs }

    override fun putIndex(id: ResourceLocation) { indexesIds += id }

    override fun putIndex(group: EarlyRegistryGroup) { groups += group }

    override fun register() {
        indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
        indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

        indexes.map { tags[it] = supplier(it) }
    }

    override fun get(idx: I) = tags[idx]
}