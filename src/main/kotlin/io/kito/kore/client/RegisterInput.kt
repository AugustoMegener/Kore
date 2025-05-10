package io.kito.kore.client

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.client.KeyMapping
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KProperty1
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterInput {

    @Scan
    companion object {

        @ObjectScanner(Any::class)
        fun registerInputs(info: IModInfo, container: ModContainer, data: Any) {

            for (fld in data::class.memberProperties.filter { it.hasAnnotation<RegisterInput>() }) {
                fld as KProperty1<Any, Any>

                container.eventBus?.addListener { event: RegisterKeyMappingsEvent ->
                    event.register(
                        fld.get(data) as? KeyMapping ?:
                        throw IllegalStateException(
                            "${fld.name} on ${data::class.qualifiedName} do not return a " +
                                    "${KeyMapping::class.qualifiedName}"
                        )
                    )
                }

                fld.isAccessible = true
                val action = (fld.getDelegate(data) as? InputRegistry)?.action ?: continue

                EVENT_BUS.addListener { event: ClientTickEvent.Post ->
                    if ((fld.get(data) as KeyMapping).consumeClick()) action()
                }
            }
        }
    }
}
