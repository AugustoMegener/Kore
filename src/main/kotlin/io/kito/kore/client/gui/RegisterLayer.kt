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

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterLayer(val id: String, val mode: LayerRegisterMode, val target: String = "") {

    enum class LayerRegisterMode { ABOVE, BELOW }

    @Scan
    companion object : FunScanner<Unit> {
        override val bound = globalBound
        override val annotation = RegisterLayer::class
        override val returnType = Unit::class

        override fun validateParameters(parms: List<KParameter>) =
            parms.size == 3 &&
                    parms[1].type.jvmErasure == GuiGraphics::class &&
                    parms[2].type.jvmErasure == DeltaTracker::class

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
