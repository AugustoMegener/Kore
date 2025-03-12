package io.kito.kore_tests.common.level.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.DataComponentTypeRegister
import io.kito.kore_tests.ID

@Scan
object DataComponents : DataComponentTypeRegister(ID) {

}