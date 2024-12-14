package io.kito.kore.util

import org.objectweb.asm.Type
import java.util.*

fun String.toTitle() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
fun String.toLower() = replaceFirstChar { it.lowercase(Locale.getDefault()) }

fun String.pascalCased() = split("_").joinToString("") { it.toTitle() }.toTitle()
fun String.camelCased() = split("_").joinToString("") { it.toTitle() }.toLower()

fun String.snakeCased() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase(Locale.getDefault())

val Type.clazz get() = Class.forName(className)
val Type.klass get() = Class.forName(className).kotlin