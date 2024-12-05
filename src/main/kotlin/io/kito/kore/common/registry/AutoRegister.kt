package io.kito.kore.common.registry

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

abstract class AutoRegister<T>(id: String, registryName: ResourceLocation) {
    private val register = DeferredRegister.create<T>(registryName, id)

    operator fun <O : T> String.invoke(supplier: () -> O): DeferredHolder<T, O> = register.register(this, supplier)

    fun register(bus: IEventBus) { register.register(bus) }
}