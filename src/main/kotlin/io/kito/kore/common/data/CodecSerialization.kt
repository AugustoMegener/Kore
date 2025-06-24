package io.kito.kore.common.data

import com.mojang.serialization.DynamicOps
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import kotlin.reflect.KType

/**
 * An implementation of [SerializationStrategy] that uses Mojang's `Codec` for serialization and deserialization.
 * This strategy is designed to work with data structures that can be represented by a `DynamicOps` instance,
 * typically used for NBT or JSON serialization within Minecraft.
 *
 * @param T The type of the data representation (e.g., `NbtElement`, `JsonElement`).
 * @property ops The [DynamicOps] instance used for interacting with the data representation.
 */
class CodecSerialization<T : Any>(val ops: DynamicOps<T>) : SerializationStrategy<T> {
    /**
     * Encodes a given value into the target data representation using its associated `Codec`.
     *
     * @param D The type of the value to encode.
     * @param value The instance of the value to encode.
     * @param valueType The [KType] of the value, used to retrieve the appropriate `Codec`.
     * @return The encoded value in the target data representation [T].
     * @throws IllegalStateException if the encoding fails.
     */
    override fun <D : Any> encode(value: D, valueType: KType): T =
        valueType.codec<D>().encodeStart(ops, value).orThrow

    /**
     * Decodes data from the target data representation into a Kotlin object using its associated `Codec`.
     *
     * @param D The type of the value to decode.
     * @param data The data in the target representation [T] to decode.
     * @param oldValue An optional old value (unused in this stateless implementation).
     * @param valueType The [KType] of the value, used to retrieve the appropriate `Codec`.
     * @return A [DecodeResult.Stateless] containing the decoded value.
     * @throws IllegalStateException if the decoding fails.
     */
    override fun <D : Any> decode(data: T, oldValue: D?, valueType: KType) =
        DecodeResult.Stateless(valueType.codec<D>().parse(ops, data).orThrow)
}

