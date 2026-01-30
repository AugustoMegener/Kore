package io.kito.kore_tests.common.resource

import io.kito.kore.common.resource.RegisterReloadListener
import io.kito.kore.common.resource.ScriptObjectReloadListener
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.resource.NiceDslScriptReloadListener.NICE_DSL_SCRIPT_LISTENER
import io.kito.kore_tests.common.script.ComponentText
import io.kito.kore_tests.common.script.DslNiceScript
import io.kito.kore_tests.logger
import net.minecraft.resources.ResourceLocation
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

@RegisterReloadListener(NICE_DSL_SCRIPT_LISTENER)
object NiceDslScriptReloadListener : ScriptObjectReloadListener<ComponentText>(KotlinType(DslNiceScript::class)) {

    const val NICE_DSL_SCRIPT_LISTENER = "$ID:nice_dsl_script_listener"

    var texts = mapOf<ResourceLocation, ComponentText>(); private set

    override fun applyResult(obj: Map<ResourceLocation, ComponentText>) {
        texts += obj
    }

    override fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure) =
        null.also { logger.error("Fail on loading $loc script:\n" + res.reports.joinToString("\n")) }
}