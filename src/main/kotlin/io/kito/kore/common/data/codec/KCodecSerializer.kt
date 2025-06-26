package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicCodec
import io.kito.kore.util.snakeCased
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A generic serializer/deserializer for Kotlin classes using Mojang's [Codec] system.
 * This class automatically generates a [Codec] for a given [KClass] by inspecting its properties
 * annotated with [Save] and using its primary constructor or a constructor annotated with [DeserializerConstructor].
 *
 * @param T The type of the class that this serializer will handle.
 * @param clazz The [KClass] instance representing the type [T].
 */
open class KCodecSerializer<T : Any>(clazz: KClass<T>) {

    /**
     * Lazily initialized list of properties from the [clazz] that are annotated with [Save].
     * Each element is a pair: the [Codec] for the property's type and the [KProperty1] representing the property itself.
     */
    private val fields  by lazy {
        clazz.memberProperties.filter { it.hasAnnotation<Save>() }.map { it.returnType.codec to it }
    }

    /**
     * The constructor to be used for deserialization.
     * It first looks for a constructor annotated with [DeserializerConstructor].
     * If no such constructor is found, it defaults to the primary constructor of the class.
     */
    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
                        ?: clazz.primaryConstructor!!

    /**
     * Initializes the [CodecSource] for this [clazz].
     * This ensures that the generated [codec] for [T] is available through [CodecSource.codec].
     */
    init { CodecSource.sources[clazz] = { _ -> codec } }

    /**
     * The generated [Codec] for the class [T].
     * This codec is responsible for encoding and decoding instances of [T] to/from a dynamic representation.
     * It uses the [Save] annotated fields for encoding and the chosen constructor for decoding.
     */
    val codec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicCodec<T>(
        // Defines the fields for encoding, using the Save annotation's ID or snake_cased property name.
        fields.map {
            (it.first as Codec<Any>)
                .fieldOf(it.second.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                    ?: it.second.name.snakeCased())
                .forGetter { o -> it.second.get(o) }
        }, ::decode
    ))
    }

    /**
     * Decodes a list of values into an instance of [T].
     * This method is used internally by the generated [codec] during deserialization.
     * It attempts to map the provided values to the constructor parameters and then to mutable properties.
     *
     * @param values A list of decoded values corresponding to the fields.
     * @return A new instance of [T] populated with the decoded values.
     */
    @Suppress(UNCHECKED_CAST)
    open fun decode(values: List<Any>): T {
        var flds = ArrayList(fields).map { it.second }.withIndex()
        val constructorFlds = new.parameters.mapNotNull { flds.find { (_, f) -> it.name == f.name } }
                                            .also       { flds -= it }

        val nonConstructor: List<Any>


        val obj = new.call(*constructorFlds.mapNotNull { values.withIndex().find { (i, _) -> i == it.index }?.value }
                                           .also       { nonConstructor = values - it.toSet()              }
                                           .toTypedArray())

        nonConstructor.withIndex().mapNotNull { (i, v) -> flds.find { (ii, _) -> i == ii }?.let { it.value to v } }
            .forEach { (fld, vl) -> (fld as? KMutableProperty1<T, Any>)?.set(obj, vl) }

        return obj
    }

    /**
     * Encodes the current instance of [T] into a dynamic representation using the generated [codec].
     * @param E The type of the dynamic operations (e.g., [net.minecraft.nbt.Tag], [com.google.gson.JsonElement]).
     * @param ops The [DynamicOps] instance to use for encoding.
     * @return The encoded representation of the object.
     */
    fun <E> T.encode(ops: DynamicOps<E>): E = codec.encodeStart(ops, this).orThrow

    /**
     * Encodes the current instance of [T] partially into a dynamic representation.
     * This is useful for debugging or when only a subset of the data is needed.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for encoding.
     * @return The partially encoded representation of the object.
     */
    fun <E> T.encodePartial(ops: DynamicOps<E>): E = codec.encodeStart(ops, this).partialOrThrow

    /**
     * Safely encodes the current instance of [T] into a dynamic representation.
     * Returns `null` if the encoding process fails.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for encoding.
     * @return The encoded representation of the object, or `null` if encoding fails.
     */
    fun <E> T.safeEncode(ops: DynamicOps<E>) = codec.encodeStart(ops, this).takeIf { it.isSuccess }?.orThrow

    /**
     * Safely encodes the current instance of [T] partially into a dynamic representation.
     * Returns `null` if the encoding process fails or no partial result is available.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for encoding.
     * @return The partially encoded representation of the object, or `null`.
     */
    fun <E> T.safeEncodePartial(ops: DynamicOps<E>) =
        codec.encodeStart(ops, this).takeIf { it.hasResultOrPartial() }?.partialOrThrow

    /**
     * Decodes data from a dynamic representation into an instance of [T] using the generated [codec].
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @param data The dynamic data to decode.
     * @return A new instance of [T] populated with the decoded data.
     */
    fun <E> decode(ops: DynamicOps<E>, data: E): T = codec.parse(ops, data).orThrow

    /**
     * Decodes data partially from a dynamic representation into an instance of [T].
     * This is useful for debugging or when only a subset of the data is needed.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @param data The dynamic data to decode.
     * @return A new instance of [T] populated with the partially decoded data.
     */
    fun <E> decodePartial(ops: DynamicOps<E>, data: E): T = codec.parse(ops, data).partialOrThrow

    /**
     * Safely decodes data from a dynamic representation into an instance of [T].
     * Returns `null` if the decoding process fails.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @param data The dynamic data to decode.
     * @return A new instance of [T] populated with the decoded data, or `null` if decoding fails.
     */
    fun <E> safeDecode(ops: DynamicOps<E>, data: E) = codec.parse(ops, data).takeIf { it.isSuccess }?.orThrow
    /**
     * Safely decodes data partially from a dynamic representation into an instance of [T].
     * Returns `null` if the decoding process fails or no partial result is available.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @param data The dynamic data to decode.
     * @return A new instance of [T] populated with the partially decoded data, or `null`.
     */
    fun <E> safeDecodePartial(ops: DynamicOps<E>, data: E) =
        codec.parse(ops, data).takeIf { it.hasResultOrPartial() }?.partialOrThrow
}

