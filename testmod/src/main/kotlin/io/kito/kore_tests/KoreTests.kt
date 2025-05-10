package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.reflect.Scan

@KMod
fun init() {

}

@Scan
object DataGenerator : DataGenHelper(ID)