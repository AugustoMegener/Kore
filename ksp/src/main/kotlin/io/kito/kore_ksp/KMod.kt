package io.kito.kore_ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import io.kito.kore.KMod
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.event.KSubscriptionsOn
import io.kito.kore.common.registry.AutoRegister
import io.kito.kore.common.registry.KRegister
import io.kito.kore.pascalCased
import io.kito.kore.snakeCased
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


const val MODS_TOML = "META-INF/neoforge.mods.toml"

private val loader = Unit::class.java.classLoader

private val kmod = KMod::class
private val register = KRegister::class
private val event = KSubscribe::class
private val subscribeEvent = KSubscriptionsOn::class

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

        /*processRegistry(modId, pack, resolver.getSymbolsWithAnnotation(register.qualifiedName!!)
            .map { it to it.getAnnotationsByType(register).first() })*/

        generateModEntrypoint(modId, pack, fn)

        return listOf(fn)
    }

    private fun processRegistry(modId: String, pack: String, entries: Sequence<Pair<KSAnnotated, KRegister>>) {

        val className = "${modId.pascalCased()}Registries"
        val obj = TypeSpec.objectBuilder(className)

        val src = mutableListOf<KSFile>()

        entries.groupBy {
            it.second
                .registry
            .takeIf { c -> c != Nothing::class } ?:
                          it.second.registry.java.enclosingClass.kotlin }
               .mapValues { it.value.map { v -> v.first to (v.second.name.takeIf { s -> s.isNotEmpty() } ?:
                                                            v.first.origin.name.snakeCased()) } }
               .forEach { (registryClass, registries) ->

                   val registry = registryClass.objectInstance as? AutoRegister<*> ?:
                        throw IllegalArgumentException("$register Class does not extend the ${AutoRegister::class} " +
                                                   "class and cannot be used in this context")

                   val valWise         = mutableListOf<Pair<KSPropertyDeclaration, String>>()
                   val objectWise      = mutableListOf<Pair<KSClassDeclaration,    String>>()
                   val constructorWise = mutableListOf<Pair<KSFunctionDeclaration, String>>()

                   for ((item, id) in registries) {
                        when (item) {
                            is KSPropertyDeclaration ->
                            { valWise += item to id; src += item.containingFile ?: item.parent?.containingFile!! }

                            is KSClassDeclaration    ->
                            { objectWise += item to id; src += item.containingFile ?: item.parent?.containingFile!! }

                            is KSFunctionDeclaration ->
                            { constructorWise += item to id; src += item.containingFile ?: item.parent?.containingFile!! }
                        }
                   }

                   for ((prop, id) in valWise) {
                       obj.addProperty(PropertySpec
                           .builder("${prop.simpleName}Registry", prop.type.origin.declaringJavaClass.kotlin)
                           .initializer("%T.new(%S) { %M }", registryClass, id, prop.simpleName,
                               prop.run { MemberName(packageName.asString(), simpleName.asString()) }).build())

                   }
            }


        codeGenerator.createNewFile(Dependencies(false, *src.toTypedArray()), pack, className).use {
            it.bufferedWriter().use { w -> FileSpec.builder(pack, className).addType(obj.build()).build().writeTo(w) }
        }
    }

    private fun generateModEntrypoint(modId: String, pack: String, fn: KSFunctionDeclaration) {
        val objName = modId.pascalCased()
        val fileName = "${objName}Mod"

        val logger =  LogManager::class.java.run { ClassName(packageName, simpleName) }

        val fileSpec = FileSpec.builder(pack, fileName)
            .addProperty(PropertySpec.builder(ID, String::class, KModifier.CONST).initializer("%S", modId).build())
            .addProperty(PropertySpec.builder(LOGGER, Logger::class).initializer("%T.getLogger(%N)", logger, ID).build())
            .addType(TypeSpec.objectBuilder(objName)
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

