package io.kito.kore_tests.common.registry.early

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.early.EarlyRegister
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry

@Scan
object Strings : EarlyRegister<String>(ID, stringRegistry) {

    val nice by "nice" of { "nice" }
    val fool by "fool" of { "fool" }
    val cute by "cute" of { "cute" }
    val weird by "weird" of { "weird" }
}