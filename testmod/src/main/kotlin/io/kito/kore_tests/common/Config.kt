package io.kito.kore_tests.common

import io.kito.kore.common.config.KConfig
import io.kito.kore.common.reflect.Scan
import net.neoforged.fml.config.ModConfig.Type.COMMON


@Scan
object Config : KConfig(COMMON) {

    val inputMsg: String? by Value { define("input_msg", "Hello world!") }
}

