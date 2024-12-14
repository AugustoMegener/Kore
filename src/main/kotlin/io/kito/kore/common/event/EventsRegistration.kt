package io.kito.kore.common.event

import io.kito.kore.Kore.logger
import io.kito.kore.reflect.FunScanner
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.event.IModBusEvent
import net.neoforged.neoforgespi.language.IModInfo
import net.neoforged.neoforgespi.language.ModFileScanData
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

private val neoEvent = net.neoforged.bus.api.Event::class.java

fun registerKoreEvents(scanData: ModFileScanData, modBus: IEventBus) {

    scanData.classes.map { Class.forName(it.clazz.className) }.forEach {
        val events =
            it.methods.mapNotNull { m -> m.annotations.filterIsInstance<KSubscribe>().firstOrNull()?.let { a -> m to a } }
        if (events.isEmpty()) return@forEach

        val config = it.annotations.filterIsInstance<KSubscriptionsOn>().firstOrNull()

        for (i in events) {
            if (DIST !in (config?.dist?.let { d -> arrayOf(d) } ?: i.second.dist)) continue

            val type = i.first.parameters.let { p ->
                when (p.count()) {
                    1 -> p.first()
                    0 -> "${i.first.name} cannot be an event listener due to having no argument"
                        .let { e -> logger.fatal(e); throw IllegalArgumentException(e) }
                    else -> ("${i.first.name} cannot be an event listener due to having too " +
                            "many arguments (${p.count()})")
                        .let { e -> logger.fatal(e); throw IllegalArgumentException(e) }
                }
            }.type.asSubclass(neoEvent)

            (if (IModBusEvent::class.java.isAssignableFrom(type)) modBus else FORGE_BUS)
                .addListener(type) { o -> i.first.invoke(i.first.declaringClass.kotlin.objectInstance ?: Unit, o) }
        }
    }
}

object EventScanner : FunScanner<Unit> {
    override val annotation = KSubscribe::class
    override val returnType =       Unit::class

    override fun validateParameters(parms: List<KParameter>) =
        parms.count() == 1 && parms[0].type.jvmErasure.isSubclassOf(Event::class)

    override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) {
        val modBus = container.eventBus ?: return

        val subscribe = data.annotations.filterIsInstance(annotation.java).first()
        val config = data.javaMethod!!.declaringClass.annotations.filterIsInstance<KSubscriptionsOn>().firstOrNull()

        if (DIST !in (config?.dist?.let { d -> arrayOf(d) } ?: subscribe.dist)) return

        val type = data.parameters.first().type.jvmErasure.java.asSubclass(neoEvent)

        (if (IModBusEvent::class.java.isAssignableFrom(type)) (modBus) else FORGE_BUS)
            .addListener(type) { o -> data.call(o) }
    }
}