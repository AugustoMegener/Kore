package io.kito.kore.registry

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

typealias DataComponentBuilder<T> = DataComponentType.Builder<T>.() -> DataComponentType.Builder<T>

open class DataComponentTypeRegister(final override val id: String) : AutoRegister {

    private val register: DeferredRegister.DataComponents =
        DeferredRegister.createDataComponents(DATA_COMPONENT_TYPE, id)

    infix fun <T> String.of(builder: DataComponentBuilder<T>):
            DeferredHolder<DataComponentType<*>, DataComponentType<T>> = register.registerComponentType(this, builder)

    override fun register(bus: IEventBus) {
        register.register(bus)
    }
}