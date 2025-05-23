package io.kito.kore;

import io.kito.kore.Kore.ID
import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.registerFluidTypeClientExts
import io.kito.kore.common.event.TemplateHandlingEvent.Companion.handleTemplates
import io.kito.kore.common.reflect.ClassScanner.Companion.scanClasses
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager

//import net.neoforged.fml.common.Mod as b



@Mod(ID)
object Kore {

    const val ID = "kore"
    val logger = LogManager.getLogger(ID)!!

    init {
        scanClasses()
        handleTemplates()
        registerFluidTypeClientExts()
    }
}