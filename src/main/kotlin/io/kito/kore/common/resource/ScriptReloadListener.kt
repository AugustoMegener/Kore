package io.kito.kore.common.resource

import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptDefinition
import kotlin.script.experimental.host.createScriptDefinitionFromTemplate
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

abstract class ScriptReloadListener(val script: KotlinType) :
    SimplePreparableReloadListener<Map<ResourceLocation,  ResultWithDiagnostics<EvaluationResult>>>()
{
    var hostConfig = defaultJvmScriptingHostConfiguration

    var context: KClass<*>? = null

    val host = BasicJvmScriptingHost()

    val kScript = script.fromClass!!.findAnnotation<KotlinScript>()
        ?: error("No script annotation on ${script.fromClass}")

    val converter = FileToIdConverter(kScript.filePathPattern, "." + kScript.fileExtension)

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller):
            Map<ResourceLocation, ResultWithDiagnostics<EvaluationResult>> =
        converter.listMatchingResources(resourceManager)
            .map { (id, res) -> converter.fileToId(id) to res.openAsReader().readText().toScriptSource() }
            .toMap()
            .mapValues { (id, code) ->
                val def = createScriptDefinitionFromTemplate(
                    script, hostConfig, context ?: ScriptDefinition::class,
                    { compilationConfig(id) }, { evaluationConfig(id) }
                )
                host.eval(code, def.compilationConfiguration, def.evaluationConfiguration)
            }

    open fun ScriptCompilationConfiguration.Builder.compilationConfig(id: ResourceLocation) {}
    open fun ScriptEvaluationConfiguration.Builder. evaluationConfig(id: ResourceLocation) {}
}