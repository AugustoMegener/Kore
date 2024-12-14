package io.kito.kore.reflect

import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

interface FunScanner<T : Any> {

    val annotation: KClass<out Annotation>
    val returnType: KClass<T>

    fun validateParameters(parms: List<KParameter>): Boolean

    fun use(info: IModInfo, container: ModContainer, data: KFunction<T>)

    companion object {
        private val scanners = arrayListOf<FunScanner<*>>()

        @ObjectScanner(FunScanner::class, 2)
        fun collectScanners(info: IModInfo, container: ModContainer, data: FunScanner<*>) {
            scanners += data
        }

        @Suppress("UNCHECKED_CAST")
        @ClassScanner(Any::class, priority = 3)
        fun scanFuns(info: IModInfo, container: ModContainer, data: KClass<*>) {
            for (scanner in scanners) {
                data.java.methods.mapNotNull { it.kotlinFunction }.forEach {
                    val foo = it.annotations.firstOrNull { a -> a.annotationClass == scanner.annotation }

                    if (!(it.annotations.firstOrNull { a -> a.annotationClass == scanner.annotation } != null &&
                          scanner.validateParameters(it.parameters)                  &&
                          it.returnType.jvmErasure.isSubclassOf(scanner.returnType)     )) return@forEach

                    scanner.use(info, container, it as KFunction<Nothing>)
                }
            }
        }
    }
}