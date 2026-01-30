package io.kito.kore.common.data.codec.stream

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import com.mojang.serialization.RecordBuilder
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.CodecSource
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.common.data.codec.DeserializerConstructor
import io.kito.kore.common.data.codec.stream.StreamCodecSource.Companion.streamCodec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicMapCodec
import io.kito.kore.util.minecraft.createDynamicStreamCodec
import io.kito.kore.util.snakeCased
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec

import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A comprehensive serializer/deserializer for Kotlin classes that supports both Mojang's [Codec]
 * (for map-like data structures) and Minecraft's [StreamCodec] (for byte buffer streams).
 * This class automatically generates both types of codecs for a given [KClass] by inspecting its properties
 * annotated with [Save] and using its primary constructor or a constructor annotated with [DeserializerConstructor].
 *
 * It's designed for flexible data persistence and network communication within Minecraft mods.
 *
 * @param B The type of [ByteBuf] used for stream serialization/deserialization (e.g., [io.netty.buffer.UnpooledByteBufAllocator.DEFAULT.buffer]).
 * @param T The type of the class that this serializer will handle.
 * @param clazz The [KClass] instance representing the type [T].
 * @param byteBuf The [KClass] instance representing the type [B].
 */
open class KStreamMapCodecSerializer<B: ByteBuf, T : Any>(clazz: KClass<T>, byteBuf: KClass<B>) {

    /**
     * Lazily initialized list of properties from the [clazz] that are annotated with [Save].
     * Each element is a pair: the [Codec] for the property's type and the [KProperty1] representing the property itself.
     * These fields are used for [mapCodec] generation.
     */
    private val fields by
        lazy { clazz.memberProperties.filter { it.hasAnnotation<Save>() }.map { it.returnType.codec to it } }

    /**
     * Lazily initialized list of properties from the [clazz] that are annotated with [Save].
     * Each element is a pair: the [StreamCodec] for the property's type and the [KProperty1] representing the property itself.
     * These fields are used for [streamCodec] generation.
     */
    private val streamFields by lazy {
        clazz.memberProperties
            .filter { it.hasAnnotation<Save>() }
            .map { it.returnType.streamCodec to it }
    }

    /**
     * The constructor to be used for deserialization.
     * It first looks for a constructor annotated with [DeserializerConstructor].
     * If no such constructor is found, it defaults to the primary constructor of the class.
     */
    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
        ?: clazz.primaryConstructor!!

    /**
     * Initializes both [CodecSource] and [StreamCodecSource] for this [clazz] and [byteBuf] type.
     * This ensures that the generated [mapCodec] and [streamCodec] for [T] are available through their respective sources.
     */
    init {
        CodecSource.sources[clazz] = { _ -> mapCodec.codec() }
        StreamCodecSource.Companion.sources += byteBuf to { _ -> streamCodec }
    }

    /**
     * The generated [Codec] for the class [T], suitable for map-like serialization.
     * This codec is responsible for encoding and decoding instances of [T] to/from a dynamic map representation.
     * It uses the [Save] annotated fields for encoding and the chosen constructor for decoding.
     */
    val mapCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicMapCodec<T>(
            // Defines the fields for encoding, using the Save annotation's ID or snake_cased property name.
            fields.map {
                (it.first as Codec<Any>)
                    .fieldOf(it.second.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                        ?: it.second.name.snakeCased())
                    .forGetter { o -> it.second.get(o) }
            }, ::mapDecode
        ))
    }

    /**
     * Decodes a list of values into an instance of [T] for map-like deserialization.
     * This method is used internally by the generated [mapCodec] during deserialization.
     * It attempts to map the provided values to the constructor parameters and that to mutable properties.
     *
     * @param values A list of decoded values corresponding to the fields.
     * @return A new instance of [T] populated with the decoded values.
     */
    @Suppress(UNCHECKED_CAST)
    open fun mapDecode(values: List<Any>): T {
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
     * Encodes the given data object [T] into a [RecordBuilder] using the generated [mapCodec].
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for encoding.
     * @param data The instance of [T] to encode.
     * @return The [RecordBuilder] with the encoded data.
     */
    fun <E> RecordBuilder<E>.encode(ops: DynamicOps<E>, data: T): RecordBuilder<E> =
        mapCodec.encode<E>(data, ops, this)

    /**
     * Decodes data from a [MapLike] representation into an instance of [T] using the generated [mapCodec].
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @return A new instance of [T] populated with the decoded data.
     */
    fun <E> MapLike<E>.decode(ops: DynamicOps<E>): T =
        mapCodec.decode(ops, this).orThrow
    /**
     * Decodes data partially from a [MapLike] representation into an instance of [T].
     * This is useful for debugging or when only a subset of the data is needed.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @return A new instance of [T] populated with the partially decoded data.
     */
    fun <E> MapLike<E>.decodePartial(ops: DynamicOps<E>): T =
        mapCodec.decode(ops, this).partialOrThrow

    /**
     * Safely decodes data from a [MapLike] representation into an instance of [T].
     * Returns `null` if the decoding process fails.
     * @param E The type of the dynamic operations.
     * @param ops The [DynamicOps] instance to use for decoding.
     * @return A new instance of [T] populated with the decoded data, or `null` if decoding fails.
     */
    fun <E> MapLike<E>.safeDecode(ops: DynamicOps<E>): T? =
        mapCodec.decode(ops, this).result().getOrNull()


    /**
     * The generated [StreamCodec] for the class [T], suitable for byte buffer serialization.
     * This codec is responsible for encoding and decoding instances of [T] to/from a [ByteBuf].
     * It uses the [Save] annotated fields for encoding and the chosen constructor for decoding.
     */
    val streamCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicStreamCodec(
            streamFields.map { (it.first as StreamCodec<B, Any>) to { o -> it.second.get(o) } },
            ::streamDecode
        ))
    }

    /**
     * Decodes a list of values from a [ByteBuf] into an instance of [T] for stream deserialization.
     * This method is used internally by the generated [streamCodec] during deserialization.
     * It attempts to map the provided values to the constructor parameters and that to mutable properties.
     *
     * @param values A list of decoded values corresponding to the fields.
     * @return A new instance of [T] populated with the decoded values.
     */
    @Suppress(UNCHECKED_CAST)
    open fun streamDecode(values: List<Any>): T {
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
     * Encodes the given data object [T] into this [ByteBuf] using the generated [streamCodec].
     * @receiver The [ByteBuf] to write the data to.
     * @param data The instance of [T] to encode.
     */
    fun B.put(data: T) { streamCodec.encode(this, data) }

    /**
     * Reads and decodes an instance of [T] from this [ByteBuf] using the generated [streamCodec].
     * @receiver The [ByteBuf] to read the data from.
     * @return A new instance of [T] populated with the decoded data.
     */
    fun B.read(): T = streamCodec.decode(this)
}

