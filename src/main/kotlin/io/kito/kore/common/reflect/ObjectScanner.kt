package io.kito.kore.common.reflect

import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

/**
 * Annotation used to mark functions that act as object scanners.
 * These functions are responsible for discovering and processing singleton objects (Kotlin `object`s)
 * that meet specific criteria. The scanner will iterate through all scanned objects and plus the
 * annotated function for each object that is a subclass of the specified `clazz`.
 *
 * @property clazz The base [KClass] that the scanned objects must be a subclass of.
 * @property priority An integer representing the priority of this scanner. Scanners with lower priority values are executed first.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ObjectScanner(val clazz: KClass<*>, val priority: Int = 0) {

    /**
     * Companion object responsible for managing and executing object scanners.
     * Annotated with `@Scan` to be automatically discovered by Kore.
     */
    @Scan
    companion object {

        /**
         * A map to store registered object scanners, keyed by their priority.
         * Each value is a list of pairs, where the first element is the target [KClass]
         * and the second is the [KFunction] that performs the scanning.
         */
        private val objectScanners = hashMapOf<Int, ArrayList<Pair<KClass<*>, KFunction<*>>>>()

        /**
         * Scans for functions annotated with [ObjectScanner] and registers them.
         * This function is invoked by Kore during mod initialization to collect all object scanner definitions.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KClass] currently being scanned for object scanner functions.
         */
        @ClassScanner(Any::class)
        fun scanObjectScanners(info: IModInfo, container: ModContainer, data: KClass<out Any>) {
            data.java.methods.mapNotNull { it.kotlinFunction }.forEach { fn ->
                val scanner = fn.findAnnotation<ObjectScanner>() ?: return@forEach

                objectScanners.computeIfAbsent(scanner.priority) { arrayListOf() } .add(scanner.clazz to fn)
            }
        }

        /**
         * Scans for singleton objects and processes them using the registered object scanners.
         * This function is invoked by Kore during mod initialization after all object scanners have been collected.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KClass] representing a singleton object to be scanned.
         */
        @ClassScanner(Any::class, priority = 1)
        fun scanObjects(info: IModInfo, container: ModContainer, data: KClass<out Any>) {
            objectScanners.toList()
                .sortedBy { it.first } // Sort scanners by priority.
                .map      { it.second }
                .forEach  { actual ->
                    for ((cls, fn) in actual) {
                        // Check if the current object is a subclass of the scanner's target class.
                        if (!data.isSubclassOf(cls))     continue
                        // Get the singleton instance of the object.
                        val obj = data.objectInstance ?: continue

                        // Invoke the scanner function, passing the mod info, container, and the scanned object.
                        // It tries to call with an object instance if available, otherwise directly.
                        fn.javaMethod!!.declaringClass.kotlin.objectInstance
                                ?.let { fn.call(it, info, container, obj) }
                                ?:run { fn.call(    info, container, obj) }
                    }
                }
        }
    }
}

