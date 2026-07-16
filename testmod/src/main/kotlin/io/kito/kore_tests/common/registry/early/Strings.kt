package io.kito.kore_tests.common.registry.early

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.early.EarlyRegister
import io.kito.kore.common.registry.early.EarlyRegistryGroup
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry

@Scan
object Strings : EarlyRegister<String>(ID, stringRegistry) {

    val myGroup = EarlyRegistryGroup(local("my_group"))

    val nice by "nice" of { "nice" } onGroup myGroup
    val fool by "fool" of { "fool" } onGroup myGroup
    val cute by "cute" of { "cute" } onGroup myGroup
    val weird by "weird" of { "weird" } onGroup myGroup
}