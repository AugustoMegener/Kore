package io.kito.kore

import io.kito.kore.Kore.ID
import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.registerFluidTypeClientExts
import io.kito.kore.common.event.TemplateHandlingEvent.Companion.handleTemplates
import io.kito.kore.common.reflect.ClassScanner.Companion.scanClasses
import io.kito.kore.common.registry.early.RegisterEarlyRegistry.Companion.registerEarlyRegistries
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager


@Mod(ID)
object Kore {

    const val ID = "kore"
    val logger = LogManager.getLogger(ID)!!

    init {
        scanClasses()
        registerEarlyRegistries()
        handleTemplates()
        registerFluidTypeClientExts()
    }

    fun local(path: String) = loc(ID, path)
}
