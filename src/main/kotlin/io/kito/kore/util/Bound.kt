package io.kito.kore.util

import io.kito.kore.util.Bound.Type.GLOBAL
import io.kito.kore.util.Bound.Type.LOCAL

sealed class Bound {

    enum class Type { GLOBAL, LOCAL }

    data object Global                 : Bound()
    data  class  Local(val id: String) : Bound()

    fun isOnBound(modId: String) = when (this) { is Global -> true; is Local -> id == modId }

    companion object {
        operator fun invoke(bound: Type, id: String) = when(bound) { GLOBAL -> Global; LOCAL -> Local(id) }
    }
}