package io.kito.kore.client.gui.kanvas

import io.kito.kore.client.gui.kanvas.KvsScript.CompilationConfig
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm


@KotlinScript(
    fileExtension = "kvs.kts",
    filePathPattern = "scripts/kanvas",
    compilationConfiguration = CompilationConfig::class,
)
abstract class KvsScript {

    class CompilationConfig : ScriptCompilationConfiguration({

        jvm {
            defaultImports()

            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    })

}