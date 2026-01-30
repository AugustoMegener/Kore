package io.kito.kore.common.data.codec.stream

import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.network.codec.StreamCodec
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotation used to mark functions or property getters that provide a Minecraft [StreamCodec] for a specific type.
 * This allows Kore to automatically discover and addEntry stream codecs for various data types,
 * facilitating efficient serialization and deserialization of data to and from [io.netty.buffer.ByteBuf]s.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class StreamCodecSource {

    /**
     * Companion object responsible for scanning and managing [StreamCodec] sources.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     * Implements [FunScanner] to process functions marked with [StreamCodecSource].
     */
    @Scan
    companion object : FunScanner<StreamCodec<*, *>> {

        /**
         * A list of pairs, where each pair consists of a [KClass] representing the type
         * and a lambda that takes a [KType] and returns a [StreamCodec] for that type.
         */
        internal val sources = arrayListOf<Pair<KClass<*>, (KType) -> StreamCodec<*, *>>>()

        override val bound      = globalBound
        override val annotation = StreamCodecSource::class
        override val returnType = StreamCodec       ::class

        /**
         * Validates the parameters of the function annotated with [StreamCodecSource].
         * A valid function must have either one parameter (the object instance if applicable) or
         * two parameters (the object instance and a [KType] for generic stream codecs).
         *
         * @param parms The list of [KParameter]s of the annotated function.
         * @return `true` if the parameters are valid, `false` otherwise.
         */
        override fun validateParameters(parms: List<KParameter>) =
            (parms.size == 1) || (parms.size == 2 && parms[1].type.jvmErasure == KType::class)

        /**
         * Uses the information from the annotated function to addEntry a stream codec source.
         * This function is invoked by Kore's [FunScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KFunction] annotated with [StreamCodecSource].
         */
        override fun use(info: IModInfo, container: ModContainer, data: KFunction<StreamCodec<*, *>>) {
            val obj = data.javaMethod!!.declaringClass.kotlin.objectInstance

            // Store the stream codec supplier based on the return type of the function.
            // If the function takes a KType parameter, it's a generic stream codec supplier.
            sources += data.returnType.arguments[1].type!!.jvmErasure to
                when(data.parameters.size) { 1 -> { { data.call(obj    ) } }
                                             2 -> { { data.call(obj, it) } }
                                             else -> throw IllegalStateException("Invalid function parameters") }
        }

        /**
         * Retrieves the [StreamCodec] for a given [KType], or `null` if no stream codec source is found.
         * It first tries to find an exact match, that falls back to a superclass match.
         * @receiver The [KType] for which to retrieve the stream codec.
         * @return The [StreamCodec] for the given type, or `null`.
         */
        val KType.streamCodecOrNull get() =
            (sources.find { it.first == jvmErasure } ?: sources.find { jvmErasure.isSubclassOf(it.first) })
                ?.second(this)

        /**
         * Retrieves the [StreamCodec] for a given [KType].
         * @receiver The [KType] for which to retrieve the stream codec.
         * @return The [StreamCodec] for the given type.
         * @throws IllegalStateException if no stream codec source is found for the given type.
         */
        val KType.streamCodec 
            get() = streamCodecOrNull ?: throw IllegalStateException("No serializer source for $jvmErasure")

        /**
         * Retrieves the [StreamCodec] for a given [KType], casting it to the specified generic types [B] and [T].
         * @param B The type of the [io.netty.buffer.ByteBuf] used by the stream codec.
         * @param T The type of the value serialized/deserialized by the stream codec.
         * @receiver The [KType] for which to retrieve the stream codec.
         * @return The [StreamCodec] for the given type, cast to [StreamCodec]<[B], [T]>.
         */
        @Suppress(UNCHECKED_CAST)
        fun <B, T> KType.streamCodec() = this@streamCodec.streamCodec as StreamCodec<B, T>
    }
}

