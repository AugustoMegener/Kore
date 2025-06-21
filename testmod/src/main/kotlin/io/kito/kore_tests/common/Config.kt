package io.kito.kore_tests.common

import io.kito.kore.common.KConfig
import io.kito.kore.common.reflect.Scan
import io.kito.kore_tests.common.data.NiceData
import net.neoforged.fml.config.ModConfig.Type.COMMON

/**
 * Configuration object for the Kore Tests mod.
 * Annotated with `@Scan` to be discovered by Kore for configuration management.
 * It extends `KConfig` and specifies `COMMON` type, meaning it's a common configuration accessible by both client and server.
 */
@Scan
object Config : KConfig(COMMON) {

    /**
     * Defines a configurable input message.
     * The value is retrieved using `Value { define("input_msg", "Hello world!") }`,
     * which sets "Hello world!" as the default value if not specified in the configuration file.
     */
    val inputMsg: String by Value { define("input_msg", "Hello world!") }
}

