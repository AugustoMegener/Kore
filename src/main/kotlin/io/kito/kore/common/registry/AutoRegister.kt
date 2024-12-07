package io.kito.kore.common.registry

import net.minecraft.core.Registry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

abstract class AutoRegister<T>(id: String, registry: Registry<T>) {

    internal open val register = DeferredRegister.create(registry, id)

    operator fun <O : T> String.invoke(supplier: () -> O): DeferredHolder<T, O> =
        register.register(this, supplier)

    fun <O : T> new(id: String, supplier: () -> O) = id(supplier)

    fun register(bus: IEventBus) { register.register(bus) }
}