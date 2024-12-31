package io.kito.kore.event

import io.kito.kore.reflect.FunScanner
import io.kito.kore.reflect.Scan
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

@Scan
object EventScanner : FunScanner<Unit> {

    private val neoEvent = net.neoforged.bus.api.Event::class.java

    override val bound = FunScanner.globalBound
    override val annotation = KSubscribe::class
    override val returnType =       Unit::class

    override fun validateParameters(parms: List<KParameter>) =
        (parms.count() == 2 && parms[1].type.jvmErasure.isSubclassOf(Event::class)).also { parms[0].type.jvmErasure }

    override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) { data as KCallable<Unit>
        val modBus = container.eventBus ?: return

        val subscribe = data.annotations.filterIsInstance(annotation.java).first()
        val config = data.javaMethod!!.declaringClass.annotations.filterIsInstance<KSubscriptionsOn>().firstOrNull()

        if (DIST !in (config?.dist?.let { d -> arrayOf(d) } ?: subscribe.dist)) return

        val type = data.parameters[1].type.jvmErasure.java.asSubclass(neoEvent)

        (if (IModBusEvent::class.java.isAssignableFrom(type)) (modBus) else FORGE_BUS)
            .addListener(type) { o ->
                data.parameters
                data.call(data.javaMethod!!.declaringClass.kotlin.objectInstance!!, o) }
    }
}