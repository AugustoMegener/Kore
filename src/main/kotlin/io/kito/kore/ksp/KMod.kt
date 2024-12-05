package io.kito.kore.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import io.kito.kore.camelCased
import net.neoforged.fml.common.Mod

private val kmod = KMod::class


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class KMod(val id: String = "")

class KModProcessor(private val logger: KSPLogger, private val codeGenerator: CodeGenerator) : SymbolProcessor {

    private var runned = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (runned) return emptyList()
        runned = true

        logger.warn("GROOO")

        val fn = (resolver.getSymbolsWithAnnotation(kmod.qualifiedName!!).iterator()
            .takeIf {  it.hasNext() } ?: return emptyList())
            .let { it.next().takeIf { _ -> !it.hasNext() } ?: run {
                logger.error("KMod annotation only accepts functions inside class or in file root"); return emptyList()
            } } as KSFunctionDeclaration

        generateModEntrypoint(
            fn.getAnnotationsByType(kmod).first().id.takeIf { it.isNotEmpty() } ?: fn.packageName.getShortName(), fn
        )

        return listOf(fn)
    }

    private fun generateModEntrypoint(modId: String, fn: KSFunctionDeclaration) {
        val objName = modId.camelCased()
        val fileName = "${objName}Mod"
        val pack = fn.packageName.asString()

        val fileSpec = FileSpec.builder(pack, fileName)
            .addAliasedImport(MemberName(pack, fn.simpleName.getShortName()), MOD_INIT)
            .addProperty(PropertySpec.builder(ID, String::class, KModifier.CONST).initializer("%S", modId).build())
            .addType(TypeSpec.objectBuilder(objName)
                .addAnnotation(AnnotationSpec.builder(Mod::class).addMember("%N", "ID").build())
                .addInitializerBlock(CodeBlock.of("%N()\n", MOD_INIT)).build())
            .build()

        codeGenerator.createNewFile(Dependencies(false, fn.containingFile!!), pack, fileName)
            .use { it.bufferedWriter().use { w -> fileSpec.writeTo(w) } }
    }

    companion object {
        private const val ID = "ID"
        private const val MOD_INIT = "modInit"
    }
}

class KModProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        KModProcessor(environment.logger, environment.codeGenerator)
}
