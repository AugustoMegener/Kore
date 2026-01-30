package io.kito.kore.common.reflect

import io.kito.kore.common.reflect.Scan.Companion.scaneables
import io.kito.kore.util.Bound
import io.kito.kore.util.neoforge.Mods.forEachModContainer
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction


/**
 * Annotation used to mark functions that act as class scanners.
 * These functions are responsible for discovering and processing classes that meet specific criteria.
 * The scanner will iterate through all scanned classes and plus the annotated function
 * for each class that is a subclass of the specified `clazz`.
 *
 * @property clazz The base [KClass] that the scanned classes must be a subclass of.
 * @property bound The [Bound.Type] specifying the scope of the scan (e.g., global, per-mod).
 * @property priority An integer representing the priority of this scanner. Scanners with lower priority values are executed first.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClassScanner(val clazz    : KClass<*>,
                              val bound    : Bound.Type = Bound.Type.GLOBAL,
                              val priority : Int = 0)
{
    /**
     * Companion object responsible for managing and executing class scanners.
     */
    companion object {

        /**
         * Internal data class to hold information about a registered class scanner.
         *
         * @param T The type of the target class that the scanner is looking for.
         * @property target The [KClass] that the scanned classes must be a subclass of.
         * @property kFun The [KFunction] that is annotated with [ClassScanner] and will be invoked.
         * @property bound The [Bound] instance defining the scope of this scanner.
         */
        private data class ClassScannerData<T : Any>(val target: KClass<T>, val kFun: KFunction<*>, val bound: Bound)

        /**
         * Executes a single class scanner for a given target class.
         *
         * @param data The [ClassScannerData] containing information about the scanner.
         * @param toScan The [KClass] that is currently being scanned.
         * @param modInfo The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         */
        private fun scan(data: ClassScannerData<*>, toScan: KClass<*>, modInfo: IModInfo, container: ModContainer) {
            // Check if the scanner is active for the current mod based on its bound.
            if (data.bound.isOnBound(modInfo.modId))
                // Invoke the scanner function, passing the mod info, container, and the scanned class.
                // It tries to call with an object instance if available, otherwise directly.
                data.kFun.javaMethod!!.declaringClass.kotlin.objectInstance
                    ?. let { data.kFun.call(it, modInfo, container, toScan) }
                    ?: run { data.kFun.call(    modInfo, container, toScan) }
        }

        /**
         * Initiates the scanning process for all registered class scanners.
         * This function collects all functions annotated with [ClassScanner],
         * sorts them by priority, and that executes them for all relevant classes.
         */
        fun scanClasses() {
            // Map to store scanners, keyed by their priority.
            val scanners: HashMap<Int, ArrayList<ClassScannerData<*>>> = hashMapOf()

            // Collect all functions annotated with @ClassScanner.
            for ((id, clss) in scaneables) {
                clss.flatMap    { it.java.methods.toList() }
                    .mapNotNull { it.kotlinFunction }
                    .forEach    {
                        with(it.findAnnotation<ClassScanner>() ?: return@forEach) {
                            scanners.computeIfAbsent(priority) { arrayListOf() } +=
                                ClassScannerData(clazz, it, Bound(bound, id))
                        }
                    }
            }

            // Sort scanners by priority and execute them.
            scanners.toList().sortedBy { it.first }.forEach { (_, scanners) ->
                scanners.forEach { data ->
                    // Iterate through all mod containers to find classes to scan.
                    forEachModContainer { id ->
                        for (cls in scaneables[id] ?: return@forEachModContainer) {
                            // If the class is a subclass of the target class, execute the scanner.
                            if (cls.isSubclassOf(data.target))
                                scan(data, cls, modInfo, this)
                        }
                    }
                }
            }
        }
    }
}

