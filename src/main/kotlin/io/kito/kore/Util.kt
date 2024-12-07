package io.kito.kore

import java.util.*

fun String.toTitle() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
fun String.toLower() = replaceFirstChar { it.lowercase(Locale.getDefault()) }

fun String.pascalCased() = split("_").joinToString("") { it.toTitle() }.toTitle()
fun String.camelCased() = split("_").joinToString("") { it.toTitle() }.toLower()