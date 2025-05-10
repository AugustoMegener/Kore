package io.kito.kore

import io.kito.kore.util.minecraft.ResourceLocationExt.loc

abstract class ModUtil(private val modId: String) {

    fun local(path: String) = loc(modId, path)

    fun keyLocale(name: String) = "key.$modId.$name"
    fun keyCategoryLocale(name: String) = "key.category.$name"
}