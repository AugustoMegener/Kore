package io.kito.kore.common.event

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.template.TemplateKit
import net.neoforged.bus.api.Event
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

class TemplateHandlingEvent : Event() {

    fun register(kit: TemplateKit<*>) { kitsToApply += kit }

    @Scan
    companion object {

        val kitsToApply = arrayListOf<TemplateKit<*>>()

        fun handleTemplates() {
            MOD_BUS.post(TemplateHandlingEvent())
            kitsToApply.forEach { it.apply() }
        }

        @ObjectScanner(Any::class)
        fun collectModelLayers(info: IModInfo, container: ModContainer, data: Any) {
            data::class.memberProperties.filter { it.hasAnnotation<RegisterKit>() }.forEach {
                kitsToApply += (it.call(data) as? TemplateKit<*> ?:
                throw IllegalStateException("Property annotated with RegisterKit don't return a TemplateKit"))
            }
        }
    }

}