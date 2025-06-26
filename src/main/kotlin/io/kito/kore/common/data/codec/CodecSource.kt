package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotation used to mark functions or property getters that provide a Mojang [Codec] for a specific type.
 * This allows Kore to automatically discover and addEntry codecs for various data types,
 * facilitating serialization and deserialization processes.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CodecSource {

    /**
     * Companion object responsible for scanning and managing [Codec] sources.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     * Implements [FunScanner] to process functions marked with [CodecSource].
     */
    @Scan
    companion object : FunScanner<Codec<*>> {

        /**
         * A map storing codec suppliers, keyed by the [KClass] of the type they serialize/deserialize.
         * The value is a lambda that takes a [KType] and returns a [Codec].
         */
        internal val sources = hashMapOf<KClass<*>, (KType) -> Codec<*>>()

        override val bound      = globalBound
        override val annotation = CodecSource::class
        override val returnType = Codec      ::class

        /**
         * Validates the parameters of the function annotated with [CodecSource].
         * A valid function must have either one parameter (the object instance if applicable) or
         * two parameters (the object instance and a [KType] for generic codecs).
         *
         * @param parms The list of [KParameter]s of the annotated function.
         * @return `true` if the parameters are valid, `false` otherwise.
         */
        override fun validateParameters(parms: List<KParameter>) =
            (parms.size == 1) || (parms.size == 2 && parms[1].type.jvmErasure == KType::class)

        /**
         * Uses the information from the annotated function to addEntry a codec source.
         * This function is invoked by Kore's [FunScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KFunction] annotated with [CodecSource].
         */
        override fun use(info: IModInfo, container: ModContainer, data: KFunction<Codec<*>>) {
            val obj = data.javaMethod!!.declaringClass.kotlin.objectInstance!!

            // Store the codec supplier based on the return type of the function.
            // If the function takes a KType parameter, it's a generic codec supplier.
            sources[data.returnType.arguments.first().type!!.jvmErasure] =
                when(data.parameters.size) { 1 -> { { data.call(obj)   } }
                                             2 -> { { data.call(obj, it) } }
                                             else -> throw IllegalStateException("Invalid function parameters") }
        }

        /**
         * Retrieves the [Codec] for a given [KType], or `null` if no codec source is found.
         * @receiver The [KType] for which to retrieve the codec.
         * @return The [Codec] for the given type, or `null`.
         */
        val KType.codecOrNull
            get() =
                sources[jvmErasure]?.let { it(this) }

        /**
         * Retrieves the [Codec] for a given [KType].
         * @receiver The [KType] for which to retrieve the codec.
         * @return The [Codec] for the given type.
         * @throws IllegalStateException if no codec source is found for the given type.
         */
        val KType.codec
            get() =
                codecOrNull ?: throw IllegalStateException("No serializer source for $jvmErasure")

        /**
         * Retrieves the [Codec] for a given [KType], casting it to the specified generic type [T].
         * @param T The type of the codec's value.
         * @receiver The [KType] for which to retrieve the codec.
         * @return The [Codec] for the given type, cast to [Codec]<[T]>.
         */
        @Suppress(UNCHECKED_CAST)
        fun <T> KType.codec() = codec as Codec<T>
    }
}

