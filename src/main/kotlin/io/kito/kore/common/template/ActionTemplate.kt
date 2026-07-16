package io.kito.kore.common.template

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGroup
import net.minecraft.resources.ResourceLocation

class ActionTemplate<T>(override val registry: EarlyRegistry<T>, val action: (T) -> Unit) : Template<T, Unit> {

    override val indexesIds = arrayListOf<ResourceLocation>()
    val groups = arrayListOf<EarlyRegistryGroup>()

    override fun putAllIndexes() { indexesIds += registry.idxs }

    override fun putIndex(id: ResourceLocation) { indexesIds += id }

    override fun putIndex(group: EarlyRegistryGroup) { groups += group }

    override fun register() {
        indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
        indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

        indexes.map { action(it) }
    }

    override fun get(idx: T) = Unit
}

