package io.kito.kore.common.registry

import io.kito.kore.common.template.Template
import net.neoforged.neoforge.registries.DeferredHolder

class RegistryTemplate<I, T>(val builder: (I) -> DeferredHolder<*, T>) : Template<I, T> {
    private val entries = hashMapOf<I, DeferredHolder<*, T>>()

    override val allIdxs by lazy { entries.keys }

    override fun register(vararg idxs: I) { idxs.forEach { entries[it] = builder(it) } }

    override fun get(idx: I) = entries[idx]?.get()
}