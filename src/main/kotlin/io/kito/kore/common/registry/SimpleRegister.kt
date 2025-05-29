package io.kito.kore.common.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

open class SimpleRegister<T>(private val register: DeferredRegister<T>, override val id: String) : AutoRegister {

    constructor(id: String, registry: Registry<T>) :
            this(DeferredRegister.create( registry, id), id)

    constructor(id: String, resourceKey: ResourceKey<Registry<T>>):
            this(DeferredRegister.create(resourceKey, id), id)

    operator fun <O : T> String.invoke(supplier: () -> O): DeferredHolder<T, O> = register.register(this, supplier)

    override fun register(bus: IEventBus) { register.register(bus) }
}