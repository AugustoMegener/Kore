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

/**
 * Annotation used to mark properties that represent key input registrations.
 * When applied to a property of type [InputRegistry] (or a delegated property that provides a [KeyMapping]),
 * Kore will automatically addEntry the associated key mapping and set up its action listener.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterInput {

    /**
     * Companion object responsible for scanning and registering key inputs within the Kore framework.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {

        /**
         * Scans for properties annotated with [RegisterInput] within a given data object
         * and registers them as key mappings with NeoForge.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The object containing the properties to be scanned.
         */
        @ObjectScanner(Any::class)
        fun registerInputs(info: IModInfo, container: ModContainer, data: Any) {

            // Iterate over properties in the data object that are annotated with @RegisterInput
            for (fld in data::class.memberProperties.filter { it.hasAnnotation<RegisterInput>() }) {
                fld as KProperty1<Any, Any>

                // Add a listener to the mod's event bus for registering key mappings
                container.eventBus?.addListener { event: RegisterKeyMappingsEvent ->
                    event.register(
                        // Cast the property value to KeyMapping or throw an error if it's not
                        fld.get(data) as? KeyMapping ?:
                        throw IllegalStateException(
                            "${fld.name} on ${data::class.qualifiedName} do not return a " +
                                    "${KeyMapping::class.qualifiedName}"
                        )
                    )
                }

                // Make the field accessible to retrieve its delegate
                fld.isAccessible = true
                // Get the InputRegistry delegate if it exists
                val reg = (fld.getDelegate(data) as? InputRegistry) ?: continue

                // Add a listener to the NeoForge event bus for client tick events
                EVENT_BUS.addListener { event: ClientTickEvent.Post ->
                    // If the key is pressed and can be used, execute the registered action
                    if ((fld.get(data) as KeyMapping).consumeClick() && reg.canUse()) reg.action()
                }
            }
        }
    }
}

