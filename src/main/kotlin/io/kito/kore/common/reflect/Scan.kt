package io.kito.kore.common.reflect

import io.kito.kore.common.reflect.Scan.ScanType.CASCADING
import io.kito.kore.common.reflect.Scan.ScanType.ISOLATED
import io.kito.kore.util.getAllNestedClasses
import io.kito.kore.util.klass
import io.kito.kore.util.neoforge.Mods.forEachModFile
import io.kito.kore.util.neoforge.Mods.modContainer
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Scan
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scan(val type: ScanType = ISOLATED) {

    enum class ScanType { CASCADING, ISOLATED }

    @Internal
    companion object {

        val scaneables by lazy {
            buildMap<String, List<KClass<*>>> {
                forEachModFile {
                    put(modContainer.modId, scanResult.annotations
                        .filter { it.annotationType.klass.hasAnnotation<Scan>() }
                        .map { it.clazz.klass.findAnnotation<Scan>()?.let { annotation ->
                            when (annotation.type) {
                                ISOLATED  -> listOf(it.clazz.klass)
                                CASCADING -> getAllNestedClasses(listOf(it.clazz.klass)) } } ?: listOf(it.clazz.klass) }
                        .flatten())
                }
            }
        }
    }
}
