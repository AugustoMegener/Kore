package io.kito.kore.common.event

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.template.Template
import net.neoforged.bus.api.Event
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * An event posted on the NeoForge Mod Event Bus to trigger the application of [TemplateKit]s.
 * This event allows for a centralized mechanism to collect and apply various template kits
 * during mod initialization or at specific points in the mod lifecycle.
 */
class TemplateHandlingEvent : Event() {

    /**
     * Registers a [TemplateKit] to be applied when this event is handled.
     * @param kit The [TemplateKit] instance to addEntry.
     */
    fun register(kit: Template<*, *>) { templatesToRegister += kit }

    /**
     * Companion object responsible for handling the collection and application of [TemplateKit]s.
     * Annotated with `@Scan` to be automatically discovered by Kore.
     */
    @Scan
    companion object {

        /**
         * A mutable list to store [TemplateKit] instances that need to be applied.
         * These kits are collected from various sources and processed when `handleTemplates()` is called.
         */
        val templatesToRegister = arrayListOf<Template<*, *>>()

        /**
         * Posts a [TemplateHandlingEvent] on the Mod Event Bus and then applies all collected [TemplateKit]s.
         * This method orchestrates the template application process.
         */
        fun handleTemplates() {
            MOD_BUS.post(TemplateHandlingEventPre())
            MOD_BUS.post(TemplateHandlingEvent())
            templatesToRegister.forEach { it.register() }
        }

        /**
         * Scans for properties annotated with [RegisterTemplate] within any scanned object.
         * If such a property is found, and it returns a [TemplateKit], it is added to the `kitsToApply` list.
         *
         * This function is invoked by Kore during mod initialization to discover template kits.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The object being scanned for [RegisterTemplate] annotations.
         * @throws IllegalStateException if a property annotated with [RegisterTemplate] does not return a [TemplateKit].
         */
        @ObjectScanner(Any::class)
        fun collectTemplateKit(info: IModInfo, container: ModContainer, data: Any) {
            data::class.memberProperties.filter { it.hasAnnotation<RegisterTemplate>() }.forEach {
                ((it.call(data) as? Template<*, *>)?.let { t -> templatesToRegister += t })
            }
        }
    }

}

