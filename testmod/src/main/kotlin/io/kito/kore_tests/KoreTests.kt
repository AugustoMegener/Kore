package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.reflect.Scan
import net.minecraft.core.NonNullList
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@KMod
fun init() {

}

@Scan
object DataGenerator : DataGenHelper(ID)