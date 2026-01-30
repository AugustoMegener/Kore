package io.kito.kore.common.data.codec.stream

import io.kito.kore.common.data.codec.DeserializerConstructor
import io.kito.kore.common.data.codec.stream.StreamCodecSource.Companion.streamCodec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicStreamCodec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A generic serializer/deserializer for Kotlin classes using Minecraft's [StreamCodec] system.
 * This class automatically generates a [StreamCodec] for a given [KClass] by inspecting its properties
 * annotated with [Send] and using its primary constructor or a constructor annotated with [DeserializerConstructor].
 * It's designed for efficient serialization and deserialization of data to and from [ByteBuf]s, typically for network packets.
 *
 * @param B The type of [ByteBuf] used for serialization/deserialization (e.g., [io.netty.buffer.UnpooledByteBufAllocator.DEFAULT.buffer]).
 * @param T The type of the class that this serializer will handle.
 * @param clazz The [KClass] instance representing the type [T].
 * @param byteBuf The [KClass] instance representing the type [B].
 */
open class KStreamCodecSerializer<B: ByteBuf, T : Any>(clazz: KClass<T>, byteBuf: KClass<B>) {

    /**
     * Lazily initialized list of properties from the [clazz] that are annotated with [Send].
     * Each element is a pair: the [StreamCodec] for the property's type and the [KProperty1] representing the property itself.
     * Properties are sorted by their `ord` value from the [Send] annotation to ensure consistent serialization order.
     */
    private val fields by lazy {
        clazz.memberProperties
            .filter { it.hasAnnotation<Send>() }
            .map { it.returnType.streamCodec to it }
            .sortedBy { it.second.findAnnotation<Send>()!!.ord }
    }

    /**
     * The constructor to be used for deserialization.
     * It first looks for a constructor annotated with [DeserializerConstructor].
     * If no such constructor is found, it defaults to the primary constructor of the class.
     */
    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
                        ?: clazz.primaryConstructor!!

    /**
     * Initializes the [StreamCodecSource] for this [clazz] and [byteBuf] type.
     * This ensures that the generated [streamCodec] for [T] is available through [StreamCodecSource.streamCodec].
     */
    init { StreamCodecSource.Companion.sources += byteBuf to { _ -> streamCodec } }

    /**
     * The generated [StreamCodec] for the class [T].
     * This codec is responsible for encoding and decoding instances of [T] to/from a [ByteBuf].
     * It uses the [Send] annotated fields for encoding and the chosen constructor for decoding.
     */
    val streamCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicStreamCodec(
            fields.map { (it.first as StreamCodec<B, Any>) to { o -> it.second.get(o) } },
            ::decode
        ))
    }

    /**
     * Decodes a list of values from a [ByteBuf] into an instance of [T].
     * This method is used internally by the generated [streamCodec] during deserialization.
     * It attempts to map the provided values to the constructor parameters and that to mutable properties.
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

