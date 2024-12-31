package io.kito.kore.reflect

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

interface FunScanner<T : Any> {

    val bound      : (IModInfo, ModContainer) -> Bound
    val annotation : KClass<out Annotation>
    val returnType : KClass<T>

    fun validateParameters(parms: List<KParameter>): Boolean

    fun use(info: IModInfo, container: ModContainer, data: KFunction<T>)

    @Scan
    companion object {

        val  localBound = { it: IModInfo, _ : ModContainer -> Bound.Local(it.modId) }
        val globalBound = {  _: IModInfo, _ : ModContainer -> Bound.Global          }

        private val scanners = arrayListOf<Pair<Bound, FunScanner<*>>>()

        @ObjectScanner(FunScanner::class, 2)
        fun collectScanners(info: IModInfo, container: ModContainer, data: FunScanner<*>) {
            scanners += data.bound(info, container) to data
        }

        @Suppress(UNCHECKED_CAST)
        @ClassScanner(Any::class, priority = 3)
        fun scanFuns(info: IModInfo, container: ModContainer, data: KClass<*>) {
            for ((bound, scanner) in scanners) {
                if (!bound.isOnBound(info.modId)) continue

                data.java.methods.mapNotNull { it.kotlinFunction }.forEach { fn ->

                    if (!(fn.annotations.any { an -> an.annotationClass == scanner.annotation } &&
                          scanner.validateParameters(fn.parameters) &&
                          fn.returnType.jvmErasure.isSubclassOf(scanner.returnType))) return@forEach

                    scanner.use(info, container, fn as KFunction<Nothing>)
                }
            }
        }
    }
}