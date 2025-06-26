package io.kito.kore.common.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * A simple utility class for registering various types of objects with NeoForge.
 * This class extends [AutoRegister] and provides a convenient way to addEntry objects
 * to a given [Registry] or [ResourceKey].
 *
 * @param T The type of the objects to be registered.
 * @property register The [DeferredRegister] instance used for registration.
 * @property id The mod ID or namespace for these registrations.
 */
open class SimpleRegister<T>(private val register: DeferredRegister<T>, override val id: String) : AutoRegister {

    /**
     * Constructor that creates a [DeferredRegister] from a given [Registry].
     * @param id The mod ID or namespace.
     * @param registry The [Registry] to addEntry objects with.
     */
    constructor(id: String, registry: Registry<T>) :
            this(DeferredRegister.create( registry, id), id)

    /**
     * Constructor that creates a [DeferredRegister] from a given [ResourceKey] of a [Registry].
     * @param id The mod ID or namespace.
     * @param resourceKey The [ResourceKey] of the [Registry] to addEntry objects with.
     */
    constructor(id: String, resourceKey: ResourceKey<Registry<T>>):
            this(DeferredRegister.create(resourceKey, id), id)

    /**
     * Operator function to addEntry a new object with the given name and supplier.
     * @param O The specific type of the object to be registered, which must be a subtype of [T].
     * @param name The name of the object to addEntry.
     * @param supplier A lambda that supplies the instance of the object to be registered.
     * @return A [DeferredHolder] for the registered object.
     */
    operator fun <O : T> String.invoke(supplier: () -> O): DeferredHolder<T, O> = register.register(this, supplier)

    /**
     * Registers the [DeferredRegister] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined objects.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) { register.register(bus) }
}

