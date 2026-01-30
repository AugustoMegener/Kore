package io.kito.kore_tests.common.script

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm


@KotlinScript(
    fileExtension = "nice.kts",
    filePathPattern = "scripts/nice",
    compilationConfiguration = NiceScript.CompilationConfig::class,
)
abstract class NiceScript {

    class CompilationConfig : ScriptCompilationConfiguration({

        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    })

}