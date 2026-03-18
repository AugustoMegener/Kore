package io.kito.kore_ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import io.kito.kore.KMod
import io.kito.kore.ModUtil
import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.theme.colors.ClrScript
import io.kito.kore.client.gui.kanvas.theme.colors.KvsColor
import io.kito.kore.util.camelCased
import io.kito.kore.util.pascalCased
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.ScriptDefinition
import kotlin.script.experimental.host.createScriptDefinitionFromTemplate
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost


private val kmod = KMod::class

class KModProcessor(private val logger: KSPLogger,
                    private val codeGenerator: CodeGenerator,
                    private val options: Map<String, String>) : SymbolProcessor
{
    private var ran = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val fn = (resolver.getSymbolsWithAnnotation(kmod.qualifiedName!!).iterator()
            .takeIf { it.hasNext() } ?: return emptyList())
            .let    { it.next().takeIf { _ -> !it.hasNext() } ?:
            run { logger.error("KMod annotation only accepts functions inside class or in file root")
                  return emptyList() } } as KSFunctionDeclaration

        val modId = fn.getAnnotationsByType(kmod).first().id.takeIf { it.isNotEmpty() } ?: fn.packageName.getShortName()
        val pack = fn.packageName.asString()

        generateModEntrypoint(modId, pack, fn)
        generateKanvasColorTokens(modId, pack)

        return listOf(fn)
    }

    private fun generateModEntrypoint(modId: String, pack: String, fn: KSFunctionDeclaration) {
        val objName = modId.pascalCased()
        val fileName = "${objName}Mod"

        val logger =  LogManager::class.java.run { ClassName(packageName, simpleName) }

        val fileSpec = FileSpec.builder(pack, fileName)
            .addProperty(PropertySpec.builder(ID, String::class, KModifier.CONST).initializer("%S", modId).build())
            .addProperty(PropertySpec.builder(LOGGER, Logger::class).initializer("%T.getLogger(%N)", logger, ID).build())
            .addType(TypeSpec.objectBuilder(objName)
                .superclass(ModUtil::class).addSuperclassConstructorParameter("%N", "ID")
                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("net.neoforged.fml.common.Mod"))
                    .addMember("%N", "ID").build())
                .addInitializerBlock(CodeBlock.of("%M()\n", MemberName(fn.packageName.asString(),
                                                                       fn.simpleName.asString()))).build())
            .build()

        codeGenerator.createNewFile(Dependencies(false, fn.containingFile!!), pack, fileName)
            .use { it.bufferedWriter().use { w -> fileSpec.writeTo(w) } }
    }

    fun generateKanvasColorTokens(modId: String, pack: String){
        val projectDir = options["projectDir"]
        val projectModId = options["projectModId"]

        val resourceDir = projectDir?.let { dir ->
            projectModId?.let { id ->
                File("$dir/src/main/resources/assets/$id/theme")
            }
        }

        if (resourceDir != null && (!resourceDir.exists() || !resourceDir.isDirectory)) {
            logger.info("Skipping theme processor: projectDir = $projectDir, projectModId = $projectModId")
            return
        }

        val colorsDir = File(resourceDir, "colors")

        if (colorsDir.exists() && colorsDir.isDirectory) {
            val host = BasicJvmScriptingHost()
            val def = createScriptDefinitionFromTemplate(
                KotlinType(ClrScript::class), defaultJvmScriptingHostConfiguration, ScriptDefinition::class, {}, {})

            val name = "${modId.camelCased()}KanvasColorTokens"

            var fileSpec = FileSpec.builder(pack, name)
                .addImport("io.kito.kore.client.gui.kanvas.theme.colors.Colors.ColorToken")


            colorsDir.walk()
                .filter { it.isFile }
                .map { host.eval(it.toScriptSource(), def.compilationConfiguration, def.evaluationConfiguration) }
                .mapNotNull { res ->
                    when (res) {
                        is ResultWithDiagnostics.Failure -> {
                            res.reports.forEach { logger.error(it.render()) }
                            null
                        }
                        is ResultWithDiagnostics.Success<*> -> res.value as? Map<String, (KvsNode) -> Int>
                    }
                }
                .flatMap { it.keys }
                .forEach {
                    fileSpec = fileSpec.addProperty(
                        PropertySpec.builder(it, KvsColor::class).delegate("ColorToken()").build()
                    )
                }

            codeGenerator.createNewFile(Dependencies(false), pack, name)
                .use { it.bufferedWriter().use { w -> fileSpec.build().writeTo(w) } }
        }
    }

    companion object {
        private const val ID = "ID"
        private const val LOGGER = "logger"
        private const val MOD_INIT = "modInit"
    }
}

class KModProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        KModProcessor(environment.logger, environment.codeGenerator, environment.options)
}