package io.kito.kore.common.registry.early

import io.kito.kore.client.InputRegistry
import io.kito.kore.client.RegisterInput
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
annotation class RegisterEarlyRegistry() {

    @Scan
    companion object {

        val registries = arrayListOf<EarlyRegistry<*>>()

        @ObjectScanner(Any::class)
        fun registerInputs(info: IModInfo, container: ModContainer, data: Any) {

            for (fld in data::class.memberProperties.filter { it.hasAnnotation<RegisterEarlyRegistry>() }) {
                fld as KProperty1<Any, Any>

                registries += fld.get(data) as? EarlyRegistry<*> ?:
                    throw IllegalStateException("$fld dont return a EarlyRegistry")
            }
        }

        fun registerEarlyRegistries() {
            registries.forEach { it.register() }
        }
    }
}
