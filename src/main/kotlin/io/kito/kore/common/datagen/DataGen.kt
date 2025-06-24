package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod

/**
 * Annotation used to mark functions that should be executed during data generation.
 * This allows for automatic discovery and execution of data generation logic within Kore.
 *
 * @property dataGenerator The [KClass] of the [DataGenHelper] object that will collect and execute this data generation function.
 *                          This must be an object (singleton) class.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataGen(val dataGenerator: KClass<out DataGenHelper>) {

    /**
     * Companion object responsible for scanning and managing data generation functions.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     * Implements [FunScanner] to process functions marked with [DataGen].
     */
    @Scan
    companion object : FunScanner<Unit> {
        override val bound      = globalBound
        override val annotation = DataGen::class
        override val returnType = Unit::class

        /**
         * Validates the parameters of the function annotated with [DataGen].
         * A valid function must have zero or one parameter (the object instance if applicable).
         *
         * @param parms The list of [KParameter]s of the annotated function.
         * @return `true` if the parameters are valid, `false` otherwise.
         */
        override fun validateParameters(parms: List<KParameter>) = parms.size <= 1

        /**
         * Uses the information from the annotated function to register a data generation block.
         * This function is invoked by Kore's [FunScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KFunction] annotated with [DataGen].
         * @throws IllegalStateException if the specified `dataGenerator` is not an object.
         */
        override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) {
            // Retrieve the DataGenHelper object instance specified by the annotation.
            // Throws an exception if it's not an object (singleton).
            (data.findAnnotation<DataGen>()!!.dataGenerator.objectInstance
                ?: throw IllegalStateException("dataGenerator from ${DataGen::class} must be an object"))
                // Add the data generation function as a block to be executed by the DataGenHelper.
                .blocks += { data.call(data.javaMethod!!.declaringClass.kotlin.objectInstance) }
        }
    }
}

