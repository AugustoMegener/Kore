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

/**
 * Annotation used to mark classes or annotations for automatic scanning by Kore.
 * When a class or an annotation is marked with `@Scan`, Kore will discover it
 * and process its contents (e.g., functions or objects marked with other scanner annotations).
 *
 * @property type The [ScanType] indicating how nested classes within the annotated class should be scanned.
 *                - [ISOLATED]: Only the annotated class itself is scanned.
 *                - [CASCADING]: The annotated class and all its nested classes are scanned recursively.
 */
@Scan
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scan(val type: ScanType = ISOLATED) {

    /**
     * Enum defining the scanning behavior for nested classes.
     */
    enum class ScanType { CASCADING, ISOLATED }

    /**
     * Companion object responsible for collecting all scannable classes within the mod environment.
     * This is an internal API and should not be used directly by mod developers.
     */
    @Internal
    companion object {

        /**
         * A lazily initialized map containing all scannable classes, keyed by their mod ID.
         * The value is a list of [KClass] objects that are marked for scanning.
         * This map is built by iterating through all mod files and identifying classes
         * annotated with `@Scan` or annotations that are themselves annotated with `@Scan`.
         */
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

