package io.kito.kore.common.template

import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGoup
import io.kito.kore.util.Indexable
import net.minecraft.resources.ResourceLocation

/**
 * A generic interface for defining templates that manage a collection of items indexed by a specific type.
 * This interface extends [Indexable] to provide indexed access to the managed items.
 *
 * @param I The type of the index used to identify individual items within the template.
 * @param T The type of the items managed by this template.
 */
interface Template<I, T> : Indexable<I, T?> {

    val registry: EarlyRegistry<I>

    val indexesIds: Collection<ResourceLocation>

    val indexes: Collection<I> get() = indexesIds.mapNotNull { registry[it] }
    
    val registereds: Collection<T> get() = indexes.mapNotNull { get(it) }

    operator fun contains(idx: I) = idx in registry.all

    fun putAllIndexes()

    fun putIndex(id: ResourceLocation)

    fun putIndex(group: EarlyRegistryGoup)

    fun register()

    companion object {
        fun <I, T, E : Template<I, T>> E.includeAll() = also { it.putAllIndexes() }

        fun <I, T, E : Template<I, T>> E.include(vararg id: ResourceLocation) =
            also { id.forEach { i -> it.putIndex(i) } }

        fun <I, T, E : Template<I, T>> E.include(id: EarlyRegistryGoup) = also { it.putIndex(id) } }

        fun <I, T, E : Template<I, T>> E.include(id: Array<EarlyRegistryGoup>) =
            also { id.forEach { i -> it.putIndex(i) }
    }
}

