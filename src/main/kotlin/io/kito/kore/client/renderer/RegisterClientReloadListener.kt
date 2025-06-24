package io.kito.kore.client.renderer

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.hasAnnotation

/**
 * Annotation used to mark [PreparableReloadListener] implementations for automatic registration on the client side.
 * When a class is annotated with `@RegisterClientReloadListener` and implements [PreparableReloadListener],
 * Kore will automatically discover it and register it with NeoForge, ensuring that its `reload` method
 * is called when client-side resources are reloaded (e.g., when resource packs are changed).
 */
@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterClientReloadListener {

    /**
     * Companion object responsible for scanning and registering client-side reload listeners.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {
        /**
         * Scans for objects that implement [PreparableReloadListener] and are annotated with [RegisterClientReloadListener],
         * then registers them as client reload listeners with NeoForge.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [PreparableReloadListener] instance to be registered.
         */
        @ObjectScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: PreparableReloadListener) {
            // Ensure the PreparableReloadListener instance itself is annotated with @RegisterClientReloadListener
            if (!data::class.hasAnnotation<RegisterClientReloadListener>()) return

            // Add a listener to the mod's event bus for registering client reload listeners
            container.eventBus?.addListener { event: RegisterClientReloadListenersEvent ->
                event.registerReloadListener(data)
            }
        }
    }
}

