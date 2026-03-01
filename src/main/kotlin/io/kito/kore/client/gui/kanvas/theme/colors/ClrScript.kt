package io.kito.kore.client.gui.kanvas.theme.colors

import io.kito.kore.client.gui.kanvas.KvsScript.CompilationConfig
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm


@KotlinScript(
    fileExtension = "clr.kts",
    filePathPattern = "scripts/theme/colors",
    compilationConfiguration = CompilationConfig::class,
)
abstract class ClrScript {

    class CompilationConfig : ScriptCompilationConfiguration({

        jvm {
            defaultImports(
                "io.kito.kore.client.gui.kanvas.theme.colors.Colors.*"
            )

            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    })
}