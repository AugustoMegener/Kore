package io.kito.kore.common.registry.early

import net.minecraft.resources.ResourceLocation

class EarlyRegistry<T> {

    val entries = hashMapOf<ResourceLocation, () -> T>()
    val groupEntries = arrayListOf<Pair<ResourceLocation, EarlyRegistryGroup>>()

    lateinit var values: Map<ResourceLocation, T> private set
    val groups = hashMapOf<EarlyRegistryGroup, ArrayList<T>>()

    val all by lazy { values.values }
    val idxs by lazy { values.keys }

    fun addEntry(location: ResourceLocation, entry: () -> T) { entries[location] = entry }

    fun register() {
        values = entries.mapValues { (_, it) -> it() }
        groupEntries.forEach { (k, i) -> groups.computeIfAbsent(i) { arrayListOf() } += values[k]!! }
    }

    operator fun get(location: ResourceLocation) = values[location]

    fun locationOf(value: T) = values.entries.find { it.value == value }?.key

    fun isInGroup(value: T, group: EarlyRegistryGroup) = groups[group]?.contains(value) == true
}