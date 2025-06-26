package io.kito.kore.common.event

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.template.Template
import io.kito.kore.common.template.TemplateKit
import net.neoforged.bus.api.Event
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties


class TemplateHandlingEventPre : Event() {

}