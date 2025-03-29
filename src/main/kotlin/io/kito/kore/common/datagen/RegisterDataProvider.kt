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

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterDataProvider(val dataGenerator: KClass<out DataGenHelper>, val dist: Dist) {

    @Scan
    companion object {

        @ClassScanner(DataProvider::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: KClass<out DataProvider>) {
            val annotation = data.findAnnotation<RegisterDataProvider>() ?: return

            (annotation.dataGenerator.objectInstance
                ?: throw IllegalStateException("DataGenerator from ${DataGen::class} must be an object")).providers +=
                annotation.dist to {
                    (data.primaryConstructor ?:
                    throw IllegalStateException("DataProvider annotated with RegisterDataProvider needs a primary" +
                                                " constructor with PackOutput as argument")).call(it)
                }
        }
    }
}