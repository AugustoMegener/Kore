package io.kito.kore_ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import io.kito.kore.KMod
import io.kito.kore.ModUtil
import io.kito.kore.util.pascalCased
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


private val kmod = KMod::class

class KModProcessor(private val logger: KSPLogger, private val codeGenerator: CodeGenerator) : SymbolProcessor {

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
                .addAnnotation(AnnotationSpec.builder(Mod::class).addMember("%N", "ID").build())
                .addInitializerBlock(CodeBlock.of("%M()\n", MemberName(fn.packageName.asString(),
                                                                       fn.simpleName.asString()))).build())
            .build()

        codeGenerator.createNewFile(Dependencies(false, fn.containingFile!!), pack, fileName)
            .use { it.bufferedWriter().use { w -> fileSpec.writeTo(w) } }
    }

    companion object {
        private const val ID = "ID"
        private const val LOGGER = "logger"
        private const val MOD_INIT = "modInit"
    }
}

class KModProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        KModProcessor(environment.logger, environment.codeGenerator)
}