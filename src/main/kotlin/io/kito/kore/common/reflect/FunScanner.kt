package io.kito.kore.common.reflect

import io.kito.kore.common.reflect.Scan.Companion.scaneables
import io.kito.kore.util.Bound
import io.kito.kore.util.UNCHECKED_CAST
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

/**
 * An interface for defining a function scanner in Kore.
 * Function scanners are responsible for discovering and processing Kotlin functions
 * that are annotated with a specific annotation and meet certain criteria.
 *
 * @param T The expected return type of the functions that this scanner will process.
 */
interface FunScanner<T : Any> {

    /**
     * A lambda that defines the [Bound] for this scanner.
     * It determines the scope (e.g., global, per-mod) within which the scanner operates.
     */
    val bound      : (IModInfo, ModContainer) -> Bound
    /**
     * The [KClass] of the annotation that marks functions to be processed by this scanner.
     */
    val annotation : KClass<out Annotation>
    /**
     * The [KClass] representing the expected return type of the functions.
     */
    val returnType : KClass<T>

    /**
     * Validates the parameters of a function to determine if it's suitable for this scanner.
     * @param parms The list of [KParameter]s of the function.
     * @return `true` if the parameters are valid, `false` otherwise.
     */
    fun validateParameters(parms: List<KParameter>): Boolean

    /**
     * The core logic of the scanner, executed for each function that matches the criteria.
     * @param info The [IModInfo] of the mod being processed.
     * @param container The [ModContainer] of the mod.
     * @param data The [KFunction] that is being processed.
     */
    fun use(info: IModInfo, container: ModContainer, data: KFunction<T>)

    /**
     * Companion object responsible for managing and executing all registered function scanners.
     * Annotated with `@Scan` to be automatically discovered by Kore.
     */
    @Scan
    companion object {

        /**
         * A [Bound] lambda for scanners that operate locally within a mod.
         */
        val  localBound = { it: IModInfo, _ : ModContainer -> Bound.Local(it.modId) }
        /**
         * A [Bound] lambda for scanners that operate globally across all mods.
         */
        val globalBound = {  _: IModInfo, _ : ModContainer -> Bound.Global          }

        /**
         * A list to store all registered [FunScanner] instances along with their [Bound].
         */
        private val scanners = arrayListOf<Pair<Bound, FunScanner<*>>>()

        /**
         * Collects [FunScanner] instances during mod initialization.
         * Annotated with `@ObjectScanner` to be automatically discovered by Kore.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [FunScanner] instance to be collected.
         */
        @ObjectScanner(FunScanner::class, 2)
        fun collectScanners(info: IModInfo, container: ModContainer, data: FunScanner<*>) {
            scanners += data.bound(info, container) to data
        }

        /**
         * Scans all classes for functions that match the criteria of registered [FunScanner]s.
         * This function is invoked by Kore during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KClass] currently being scanned for functions.
         */
        @Suppress(UNCHECKED_CAST)
        @ClassScanner(Any::class, priority = 3)
        fun scanFuns(info: IModInfo, container: ModContainer, data: KClass<*>) {
            for ((bound, scanner) in scanners) {
                // Skip if the scanner is not active for the current mod.
                if (!bound.isOnBound(info.modId)) continue

                // Iterate through all functions in the class.
                data.java.methods.mapNotNull { it.kotlinFunction }.forEach { fn ->

                    // Check if the function has the required annotation, valid parameters, and correct return type.
                    if (!(fn.annotations.any { an -> an.annotationClass == scanner.annotation } &&
                          scanner.validateParameters(fn.parameters) &&
                          fn.returnType.jvmErasure.isSubclassOf(scanner.returnType))) return@forEach

                    scaneables
                    // If all criteria are met, use the scanner to process the function.
                    scanner.use(info, container, fn as KFunction<Nothing>)
                }
            }
        }
    }
}

