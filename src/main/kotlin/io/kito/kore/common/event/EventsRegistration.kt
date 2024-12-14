package io.kito.kore.common.event

import io.kito.kore.Kore.logger
import io.kito.kore.reflect.FunScanner
import io.kito.kore.util.Bound
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

object EventScanner : FunScanner<Unit> {

    override val bound = FunScanner.localBound
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