package io.kito.kore.util

import net.minecraft.resources.ResourceLocation
import org.objectweb.asm.Type
import java.util.*
import kotlin.reflect.KClass

const val UNCHECKED_CAST = "UNCHECKED_CAST"

val classLoader: ClassLoader get() = Thread.currentThread().contextClassLoader

fun String.toTitle() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
fun String.toLower() = replaceFirstChar { it.lowercase(Locale.getDefault()) }

fun String.pascalCased() = split("_").joinToString("") { it.toTitle() }.toTitle()
fun String. camelCased() = split("_").joinToString("") { it.toTitle() }.toLower()

fun String.snakeCased() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase(Locale.getDefault())

val Type.clazz: Class<*> get() = Class.forName(className, false, classLoader)
inline val Type.klass           get() = clazz.kotlin

tailrec fun getAllNestedClasses(clazzes: List<KClass<*>>,
                                result: List<KClass<*>> = emptyList()): List<KClass<*>> =
    if (clazzes.isEmpty()) result
    else getAllNestedClasses(clazzes.drop(1) + clazzes.first().java.classes.map { it.kotlin }, result + clazzes.first())


operator fun ResourceLocation.plus(loc: String): ResourceLocation = withSuffix(loc)

inline infix fun <reified T>       T .`, `(other: T) = arrayOf(this,  other)
inline infix fun <reified T> Array<T>.`, `(other: T) =         this + other