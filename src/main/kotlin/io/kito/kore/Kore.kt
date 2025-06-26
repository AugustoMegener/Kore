package io.kito.kore;

import io.kito.kore.Kore.ID
import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.registerFluidTypeClientExts
import io.kito.kore.common.event.TemplateHandlingEvent.Companion.handleTemplates
import io.kito.kore.common.reflect.ClassScanner.Companion.scanClasses
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager

/**
 * The main entry point for the Kore framework.
 * This object is annotated with `@Mod`, marking it as a NeoForged mod.
 * It handles the initialization of various Kore components, including class scanning, template handling, and client-side fluid type extensions.
 */
@Mod(ID)
object Kore {

    /**
     * The unique identifier for the Kore mod.
     */
    const val ID = "kore"
    /**
     * The logger instance for the Kore mod, used for logging messages and debugging information.
     */
    val logger = LogManager.getLogger(ID)!!

    /**
     * The initialization block for the Kore mod.
     * This block is executed when the mod is loaded by NeoForged.
     * It performs essential setup tasks:
     * - `scanClasses()`: Scans for annotated classes and functions to register various mod components.
     * - `handleTemplates()`: Processes and applies templates for different mod elements.
     * - `registerFluidTypeClientExts()`: Registers client-side extensions for fluid types, enabling custom fluid rendering.
     */
    init {
        scanClasses()
        handleTemplates()
        registerFluidTypeClientExts()
    }
}

