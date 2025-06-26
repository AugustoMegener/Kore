package io.kito.kore.common.registry

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

typealias DataComponentBuilder<T> = DataComponentType.Builder<T>.() -> DataComponentType.Builder<T>

/**
 * A utility class for registering custom [DataComponentType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of data component types
 * with NeoForge, simplifying the process of adding custom data components to items, blocks, etc.
 *
 * @property id The mod ID or namespace for these data component types.
 */
open class DataComponentTypeRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister.DataComponents] specifically for [DataComponentType]s, tied to the given mod ID.
     */
    private val register: DeferredRegister.DataComponents =
        DeferredRegister.createDataComponents(DATA_COMPONENT_TYPE, id)

    /**
     * Infix function to define a new [DataComponentType] with a builder for its properties.
     * This is the primary method for registering data component types.
     *
     * @param T The type of the data that this component will hold.
     * @param name The name of the data component type (e.g., "my_component").
     * @param builder A lambda that takes a [DataComponentType.Builder] and applies properties to it.
     * @return A [DeferredHolder] for the registered [DataComponentType].
     */
    infix fun <T> String.of(builder: DataComponentBuilder<T>):
            DeferredHolder<DataComponentType<*>, DataComponentType<T>> = register.registerComponentType(this, builder)

    /**
     * Registers the [DeferredRegister.DataComponents] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined data component types.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register.register(bus)
    }
}

