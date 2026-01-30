package io.kito.kore_tests.common.resource

import io.kito.kore.common.resource.RegisterReloadListener
import io.kito.kore.common.resource.ScriptValueReloadListener
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.resource.NiceScriptReloadListener.NICE_SCRIPT_LISTENER
import io.kito.kore_tests.common.script.NiceScript
import io.kito.kore_tests.logger
import net.minecraft.resources.ResourceLocation
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

@RegisterReloadListener(NICE_SCRIPT_LISTENER)
object NiceScriptReloadListener : ScriptValueReloadListener<(Int, Int) -> Int>(KotlinType(NiceScript::class)) {

    var lambdas = mapOf<ResourceLocation, (Int, Int) -> Int>(); private set

    const val NICE_SCRIPT_LISTENER = "$ID:nice_script_listener"

    override fun applyResult(obj: Map<ResourceLocation, (Int, Int) -> Int>) { lambdas = obj }

    override fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure) =
        null.also { logger.error("Fail on loading $loc script:\n" + res.reports.joinToString("\n")) }
}