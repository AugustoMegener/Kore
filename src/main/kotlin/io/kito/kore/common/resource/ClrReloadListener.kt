package io.kito.kore.common.resource

import io.kito.kore.Kore.ID
import io.kito.kore.Kore.local
import io.kito.kore.Kore.logger
import io.kito.kore.client.gui.kanvas.theme.colors.ColorScheme
import io.kito.kore.client.renderer.RegisterClientReloadListener
import io.kito.kore_scripts.ClrScript
import net.minecraft.resources.ResourceLocation
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.ERROR
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.FATAL

@RegisterClientReloadListener("$ID:theme_colors")
object ClrReloadListener : ScriptValueReloadListener<ColorScheme>(KotlinType(ClrScript::class)) {

    private lateinit var colors: Map<ResourceLocation, ColorScheme>

    override fun applyResult(obj: Map<ResourceLocation, ColorScheme>) { colors = obj }

    override fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure): ColorScheme? {
        logger.error(res.reports
            .filter { it.severity == FATAL || it.severity == ERROR }
            .joinToString("\n") { it.render() }
        )
        return null
    }

    fun getColors(loc: ResourceLocation): ColorScheme = (colors[local("default")] ?: mapOf()) + (colors[loc] ?: mapOf())
}