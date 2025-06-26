package io.kito.kore.common.event

import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import net.neoforged.bus.api.Event
import net.neoforged.fml.ModContainer
import net.neoforged.fml.event.IModBusEvent
import net.neoforged.neoforgespi.language.IModInfo
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * A scanner responsible for automatically registering event subscribers in Kore.
 * This object scans for functions annotated with [KSubscribe] and registers them
 * with the appropriate NeoForge event bus (Mod Bus or Forge Bus) based on the event type
 * and specified distribution.
 *
 * Annotated with `@Scan` to be automatically discovered by Kore.
 * Implements [FunScanner] to process functions marked with [KSubscribe].
 */
@Scan
object EventScanner : FunScanner<Unit> {

    /**
     * Reference to NeoForge's base [Event] class for type checking.
     */
    private val neoEvent = net.neoforged.bus.api.Event::class.java

    override val bound = FunScanner.globalBound
    override val annotation = KSubscribe::class
    override val returnType =       Unit::class

    /**
     * Validates the parameters of the function annotated with [KSubscribe].
     * A valid event subscriber function must have exactly two parameters:
     * the first being the object instance (if applicable) and the second being a subclass of [Event].
     *
     * @param parms The list of [KParameter]s of the annotated function.
     * @return `true` if the parameters are valid, `false` otherwise.
     */
    override fun validateParameters(parms: List<KParameter>) =
        (parms.count() == 2 && parms[1].type.jvmErasure.isSubclassOf(Event::class)).also { parms[0].type.jvmErasure }

    /**
     * Uses the information from the annotated function to addEntry an event listener.
     * This function is invoked by Kore during mod initialization.
     *
     * It determines which event bus to use (Mod Bus or Forge Bus) and registers the function
     * as a listener for the specified event type, respecting the distribution configuration.
     *
     * @param info The [IModInfo] of the mod being processed.
     * @param container The [ModContainer] of the mod.
     * @param data The [KFunction] annotated with [KSubscribe].
     */
    override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) { data as KCallable<Unit>
        val modBus = container.eventBus ?: return

        val subscribe = data.annotations.filterIsInstance(annotation.java).first()
        val config = data.javaMethod!!.declaringClass.annotations.filterIsInstance<KSubscriptionsOn>().firstOrNull()

        // Check if the current distribution matches the subscription configuration.
        if (DIST !in (config?.dist?.let { d -> arrayOf(d) } ?: subscribe.dist)) return

        // Get the event type from the second parameter of the function.
        val type = data.parameters[1].type.jvmErasure.java.asSubclass(neoEvent)

        // Determine which event bus to use: Mod Bus for IModBusEvent, otherwise Forge Bus.
        (if (IModBusEvent::class.java.isAssignableFrom(type)) (modBus) else FORGE_BUS)
            .addListener(type) { o ->
                data.parameters
                // Invoke the event subscriber function with the object instance and the event object.
                data.call(data.javaMethod!!.declaringClass.kotlin.objectInstance!!, o) }
    }
}

