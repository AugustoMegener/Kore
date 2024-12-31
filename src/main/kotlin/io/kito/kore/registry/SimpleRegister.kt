package io.kito.kore.registry

import net.minecraft.core.Registry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

abstract class SimpleRegister<T>(final override val id: String, registry: Registry<T>) : AutoRegister {

    private val register = DeferredRegister.create(registry, id)

    operator fun <O : T> String.invoke(supplier: () -> O): DeferredHolder<T, O> = register.register(this, supplier)

    override fun register(bus: IEventBus) { register.register(bus) }
}