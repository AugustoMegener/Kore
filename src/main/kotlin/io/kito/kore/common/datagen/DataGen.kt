package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataGen(val dataGenerator: KClass<out DataGenHelper>) {

    companion object : FunScanner<Unit> {
        override val bound      = globalBound
        override val annotation = DataGen::class
        override val returnType = Unit::class

        override fun validateParameters(parms: List<KParameter>) = parms.isEmpty()

        override fun use(info: IModInfo, container: ModContainer, data: KFunction<Unit>) {
            (data.findAnnotation<DataGen>()!!.dataGenerator.objectInstance
                ?: throw IllegalStateException("dataGenerator from ${DataGen::class} must be an object"))
                .blocks += { data.call() }
        }
    }
}
