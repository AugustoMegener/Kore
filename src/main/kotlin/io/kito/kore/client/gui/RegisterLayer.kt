package io.kito.kore.client.gui

import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotation used to mark functions that should be registered as GUI layers.
 * These functions will be called to render custom GUI elements on the screen.
 *
 * @property id The unique identifier for this GUI layer.
 * @property mode Specifies whether the layer should be rendered [ABOVE] or [BELOW] a target layer, or all layers.
 * @property target An optional identifier of another GUI layer. If provided, the layer will be registered relative to this target.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterLayer(val id: String, val mode: LayerRegisterMode, val target: String = "") {

    /**
     * Defines the rendering order of the GUI layer relative to other layers.
     */
    enum class LayerRegisterMode { ABOVE, BELOW }

    /**
     * Companion object responsible for scanning and registering GUI layers.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     * Implements [FunScanner] to process functions marked with [RegisterLayer].
     */
    @Scan
    companion object : FunScanner<Unit> {
        override val bound = globalBound
        override val annotation = RegisterLayer::class
        override val returnType = Unit::class

        /**
         * Validates the parameters of the function annotated with [RegisterLayer].
         * A valid function must have exactly three parameters: the object instance (if applicable),
         * [GuiGraphics], and [DeltaTracker].
         *
         * @param parms The list of [KParameter]s of the annotated function.
         * @return `true` if the parameters are valid, `false` otherwise.
         */
        override fun validateParameters(parms: List<KParameter>) =
            parms.size == 3 &&
                    parms[1].type.jvmErasure == GuiGraphics::class &&
                    parms[2].type.jvmErasure == DeltaTracker::class

        /**
         * Uses the information from the annotated function to addEntry a GUI layer with NeoForge.
         * This function is invoked by Kore's [FunScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KFunction] annotated with [RegisterLayer].
         */
        override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) {
            val obj = data.javaMethod!!.declaringClass.kotlin.objectInstance

            val registry = data.findAnnotation<RegisterLayer>() ?: return
            val bus = container.eventBus ?: return

            val id = registry.id.toLoc()
            val other = registry.target.takeIf { it.isNotEmpty() }?.toLoc()

            when(registry.mode) {
                LayerRegisterMode.ABOVE -> {
                    other?.also {
                        bus.addListener { event: RegisterGuiLayersEvent ->
                            event.registerAbove(it, id) { a, b -> data.call(obj, a, b) }
                        }
                    } ?: also {
                        bus.addListener { event: RegisterGuiLayersEvent ->
                            event.registerAboveAll(id) { a, b -> data.call(obj, a, b) }
                        }
                    }
                }
                LayerRegisterMode.BELOW -> {
                    other?.also {
                        bus.addListener { event: RegisterGuiLayersEvent ->
                            event.registerBelow(it, id) { a, b -> data.call(obj, a, b) }
                        }
                    } ?: also {
                        bus.addListener { event: RegisterGuiLayersEvent ->
                            event.registerBelowAll(id) { a, b -> data.call(obj, a, b) }
                        }
                    }
                }
            }
        }
    }
}

