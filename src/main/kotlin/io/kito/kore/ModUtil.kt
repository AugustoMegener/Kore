package io.kito.kore

import io.kito.kore.Kore.ID
import io.kito.kore.util.loc

abstract class ModUtil(private val modId: String) {

    fun local(path: String) = loc(ID, path)
}