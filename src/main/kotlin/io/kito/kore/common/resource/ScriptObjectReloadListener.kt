package io.kito.kore.common.resource

import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics

abstract class ScriptObjectReloadListener<T>(script: KotlinType) : ScriptReloadListener(script) {

    @Suppress(UNCHECKED_CAST)
    override fun apply(
        `object`: Map<ResourceLocation, ResultWithDiagnostics<EvaluationResult>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        applyResult(
            hashMapOf<ResourceLocation, T>().apply {
                `object`.forEach { (loc, res) ->
                    when(res) {
                        is ResultWithDiagnostics.Failure -> onFail(loc, res)?.let { put(loc, it) }
                        is ResultWithDiagnostics.Success<EvaluationResult> ->
                            when(val value = res.value.returnValue) {
                                is ResultValue.Error -> error("$loc Script error: ${value.error}")
                                ResultValue.NotEvaluated -> error("Not evaluated $loc Script")
                                is ResultValue.Unit,
                                is ResultValue.Value -> (value.scriptInstance as? T)?.let { put(loc, it) }
                            }
                    }
                }
            }
        )
    }

    abstract fun applyResult(obj: Map<ResourceLocation, T>)

    abstract fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure) : T?
}