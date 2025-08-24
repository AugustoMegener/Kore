package io.kito.kore.common.reflect

import io.kito.kore.util.clazz
import io.kito.kore.util.klass
import io.kito.kore.util.neoforge.Mods.forEachModFile
import io.kito.kore.util.neoforge.Mods.modContainer
import io.kito.kore.util.neoforge.Mods.modId
import org.jetbrains.annotations.ApiStatus.Internal
import java.lang.annotation.ElementType
import kotlin.reflect.KClass
import kotlin.streams.asSequence

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
annotation class Scan {

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

                val annotations = forEachModFile {
                    scanResult.getAnnotatedBy(Scan::class.java, ElementType.TYPE).toList()
                }.flatten().mapNotNull { it.clazz.clazz }.filter { Annotation::class.java.isAssignableFrom(it) }

                forEachModFile {
                    put(modId, annotations.flatMap {
                        scanResult.getAnnotatedBy(it as Class<out Annotation>, ElementType.TYPE).toList()
                    }.mapNotNull { it.clazz.klass })
                }
            }
        }
    }
}

