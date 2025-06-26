package io.kito.kore.util

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.ItemCapability
import org.objectweb.asm.Type
import java.util.*
import kotlin.reflect.KClass

/**
 * A constant string used to suppress unchecked cast warnings.
 */
const val UNCHECKED_CAST = "UNCHECKED_CAST"

/**
 * Extension property to get the current thread's context class loader.
 */
val classLoader: ClassLoader get() = Thread.currentThread().contextClassLoader

/**
 * Extension function to convert the first character of a string to title case.
 * @return The string with its first character in title case.
 */
fun String.toTitle() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
/**
 * Extension function to convert the first character of a string to lower case.
 * @return The string with its first character in lower case.
 */
fun String.toLower() = replaceFirstChar { it.lowercase(Locale.getDefault()) }

/**
 * Extension function to convert a snake_cased string to PascalCase.
 * Example: "my_string" -> "MyString"
 * @return The PascalCased string.
 */
fun String.pascalCased() = split("_").joinToString("") { it.toTitle() }.toTitle()
/**
 * Extension function to convert a snake_cased string to camelCase.
 * Example: "my_string" -> "myString"
 * @return The camelCased string.
 */
fun String. camelCased() = split("_").joinToString("") { it.toTitle() }.toLower()

/**
 * Extension function to convert a camelCase or PascalCase string to snake_case.
 * Example: "myString" -> "my_string", "MyString" -> "my_string"
 * @return The snake_cased string.
 */
fun String.snakeCased() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase(Locale.getDefault())

/**
 * Extension property to get the [Class] object from an ASM [Type].
 */
val Type.clazz: Class<*> get() = Class.forName(className, false, classLoader)
/**
 * Extension property to get the [KClass] object from an ASM [Type].
 */
inline val Type.klass           get() = clazz.kotlin

/**
 * Recursively finds all nested classes within a given list of [KClass]es.
 * @param clazzes The initial list of [KClass]es to scan.
 * @param result The accumulated list of nested classes (used for recursion).
 * @return A list of all nested [KClass]es found.
 */
tailrec fun getAllNestedClasses(clazzes: List<KClass<*>>,
                                result: List<KClass<*>> = emptyList()): List<KClass<*>> =
    if (clazzes.isEmpty()) result
    else getAllNestedClasses(clazzes.drop(1) + clazzes.first().java.classes.map { it.kotlin }, result + clazzes.first())

/**
 * Operator function to concatenate a [ResourceLocation] with a suffix.
 * @param loc The suffix to append.
 * @return A new [ResourceLocation] with the suffix appended to the path.
 */
operator fun ResourceLocation.plus(loc: String): ResourceLocation = withSuffix(loc)

/**
 * Infix function to create an array from two elements.
 * @param T The type of the elements.
 * @param other The second element.
 * @return An [Array] containing both elements.
 */
inline infix fun <reified T>       T .`, `(other: T) = arrayOf(this,  other)
/**
 * Infix function to append an element to an existing array.
 * @param T The type of the elements.
 * @param other The element to append.
 * @return A new [Array] with the element appended.
 */
inline infix fun <reified T> Array<T>.`, `(other: T) =         this + other

/**
 * Creates a lazy delegate that uses a context object for its initialization.
 * @param T The type of the context object.
 * @param R The type of the value to be lazily initialized.
 * @param ctx The context object.
 * @param supplier A lambda that takes the context object and returns the value.
 * @return A [Lazy] delegate for the value.
 */
inline fun <T, R> ctxLazy(ctx: T, crossinline supplier: T.() -> R) = lazy { supplier(ctx) }

