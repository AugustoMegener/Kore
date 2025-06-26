package io.kito.kore.client.gui.screens.inventory.decorator

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.client.gui.LayeredDraw
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.hasAnnotation

/**
 * Annotation used to mark [KItemDecorator] implementations for automatic registration.
 * When a class is annotated with `@RegisterDecorator` and implements [KItemDecorator],
 * Kore will automatically discover it and register its associated item decorations with NeoForge.
 */
@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterDecorator {

    /**
     * Companion object responsible for scanning and registering item decorators.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {

        /**
         * Scans for objects that implement [KItemDecorator] and are annotated with [RegisterDecorator],
         * then registers them as item decorators with NeoForge.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KItemDecorator] instance to be registered.
         */
        @ObjectScanner(KItemDecorator::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: KItemDecorator) {
            // Ensure the KItemDecorator instance itself is annotated with @RegisterDecorator
            if (!data::class.hasAnnotation<RegisterDecorator>()) return

            // Add a listener to the mod's event bus for registering item decorations
            container.eventBus?.addListener { event: RegisterItemDecorationsEvent ->
                // For each target item specified by the decorator, register the decorator
                data.targetItems.forEach { event.register(it, data) }
            }
        }
    }
}

