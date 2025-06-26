package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.ClassScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.data.DataProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * Annotation used to mark [DataProvider] implementations for automatic registration.
 * When a class is annotated with `@RegisterDataProvider`, Kore will automatically discover it
 * and register it with the specified [DataGenHelper] for the given [Dist] (client or server).
 *
 * This simplifies the process of integrating custom data generators into the modding environment.
 *
 * @property dataGenerator The [KClass] of the [DataGenHelper] object that this data provider should be registered with.
 *                          This must be an object (singleton) class.
 * @property dist The [Dist] (client or server) for which this data provider is intended.
 */
@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterDataProvider(val dataGenerator: KClass<out DataGenHelper>, val dist: Dist) {

    /**
     * Companion object responsible for scanning and registering data providers.
     * Annotated with `@Scan` to be automatically discovered by Kore
     * Implements [ClassScanner] to process classes marked with [RegisterDataProvider].
     */
    @Scan
    companion object {

        /**
         * Scans for classes that implement [DataProvider] and are annotated with [RegisterDataProvider],
         * then registers them with the appropriate [DataGenHelper].
         *
         * This function is invoked by Kore during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KClass] of the [DataProvider] to be registered.
         * @throws IllegalStateException if the specified `dataGenerator` is not an object,
         *                               or if the [DataProvider] class does not have a primary constructor
         *                               that takes a [PackOutput] as an argument.
         */
        @ClassScanner(DataProvider::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: KClass<out DataProvider>) {
            val annotation = data.findAnnotation<RegisterDataProvider>() ?: return

            // Retrieve the DataGenHelper object instance specified by the annotation.
            // Throws an exception if it's not an object (singleton).
            (annotation.dataGenerator.objectInstance
                ?: throw IllegalStateException("DataGenerator from ${DataGen::class} must be an object")).providers +=
                // Register the data provider with its distribution and a lambda to create its instance.
                annotation.dist to {
                    // Call the primary constructor of the DataProvider class with the PackOutput.
                    (data.primaryConstructor ?:
                    throw IllegalStateException("DataProvider annotated with RegisterDataProvider needs a primary" +
                                                " constructor with PackOutput as argument")).call(it)
                }
        }
    }
}

