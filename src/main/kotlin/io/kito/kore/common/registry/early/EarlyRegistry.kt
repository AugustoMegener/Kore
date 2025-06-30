package io.kito.kore.common.registry.early

import io.kito.kore.common.reflect.Scan
import net.minecraft.resources.ResourceLocation

class EarlyRegistry<T> {

    val entries = hashMapOf<ResourceLocation, () -> T>()

    lateinit var values: Map<ResourceLocation, T> private set

    val all by lazy { values.values }

    fun addEntry(location: ResourceLocation, entry: () -> T) { entries[location] = entry }

    fun register() { values = entries.mapValues { (_, it) -> it() } }

    operator fun get(location: ResourceLocation) = values[location]

    fun locationOf(value: T) = values.entries.find { it.value == value }?.key
}