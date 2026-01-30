package io.kito.kore_tests.common.script

import net.minecraft.network.chat.MutableComponent
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.jvmTarget


@KotlinScript(
    fileExtension = "dsl.kts",
    filePathPattern = "scripts/dsl",
    compilationConfiguration = DslNiceScript.CompilationConfig::class,
)
abstract class DslNiceScript : ComponentText() {

    class CompilationConfig : ScriptCompilationConfiguration({
        baseClass(ComponentText::class)

        jvm {
            jvmTarget("21")
            dependenciesFromCurrentContext(wholeClasspath = true)

        }
    })
}

fun Any.dsl(block: DslNiceScript.() -> Unit) = block(this as DslNiceScript)

abstract class ComponentText {
    lateinit var title: MutableComponent

    var lines = listOf<MutableComponent>(); private set

    fun line(component: MutableComponent) { lines += component }
}