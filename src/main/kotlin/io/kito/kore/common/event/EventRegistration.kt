package io.kito.kore.common.event

import io.kito.kore.Kore.logger
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.IModBusEvent
import net.neoforged.neoforgespi.language.ModFileScanData
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

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

            (if (type.isAssignableFrom(IModBusEvent::class.java)) modBus else MOD_BUS)
                .addListener(type) { o -> i.first.invoke(i.first.declaringClass.kotlin.objectInstance ?: Unit, o) }
        }
    }
}