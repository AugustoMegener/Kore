package io.kito.kore.common.registry

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo

/**
 * An interface for classes that can automatically register themselves with NeoForge.
 * Implementations of this interface provide a standardized way for Kore to discover
 * and register various mod components (e.g., blocks, items, fluid types) during initialization.
 */
interface AutoRegister {

    /**
     * The unique identifier (typically the mod ID) associated with this registration.
     */
    val id: String

    /**
     * Registers the component with the provided [IEventBus].
     * This method contains the logic for how the component integrates with NeoForge.
     * @param bus The [IEventBus] to register with (e.g., the Mod Event Bus).
     */
    fun register(bus: IEventBus)

    /**
     * Companion object responsible for scanning and automatically registering [AutoRegister] instances.
     * Annotated with `@Scan` to be automatically discovered by Kore.
     */
    @Scan
    companion object {
        /**
         * Scans for objects that implement [AutoRegister] and registers them with their respective mod event bus.
         *
         * This function is invoked by Kore during mod initialization to discover and activate auto-registrable components.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [AutoRegister] instance to be registered.
         */
        @ObjectScanner(AutoRegister::class)
        fun registerKoreAutoRegistries(info: IModInfo, container: ModContainer, data: AutoRegister) {
            // Register the component with the mod's event bus.
            data.register(container.eventBus ?: return)
        }
    }
}

