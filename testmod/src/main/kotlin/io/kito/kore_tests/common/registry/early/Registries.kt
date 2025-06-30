package io.kito.kore_tests.common.registry.early

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.RegisterEarlyRegistry

@Scan
object Registries {

    @RegisterEarlyRegistry
    val stringRegistry = EarlyRegistry<String>()
}