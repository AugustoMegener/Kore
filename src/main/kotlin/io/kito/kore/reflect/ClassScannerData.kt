package io.kito.kore.reflect

import io.kito.kore.reflect.ClassScanner.Bound
import io.kito.kore.reflect.ClassScanner.Bound.GLOBAL
import io.kito.kore.reflect.ClassScanner.Bound.LOCAL
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class ClassScannerData<T : Any>(val target: KClass<T>, val kFun: KFunction<*>, val bound: ScannerBound) {

    sealed class ScannerBound {
        data object Global                 : ScannerBound()
        data  class  Local(val id: String) : ScannerBound()

        fun isOnBound(modId: String) = when (this) { is Global -> true; is Local -> id == modId }

        companion object {
            operator fun invoke(bound: Bound, id: String) = when(bound) { GLOBAL -> Global; LOCAL -> Local(id) }
        }
    }


}