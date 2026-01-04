package io.kito.kore.common.data.strategy

import com.mojang.serialization.DynamicOps
import io.kito.kore.common.data.DecodeResult
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.reflect.KType

/**
 * An implementation of [SerializationStrategy] that uses Mojang's `Codec` for serialization and deserialization.
 * This strategy is designed to work with data structures that can be represented by a `DynamicOps` instance,
 * typically used for NBT or JSON serialization within Minecraft.
 *
 * @param T The type of the data representation (e.g., `NbtElement`, `JsonElement`).
 * @property ops The [com.mojang.serialization.DynamicOps] instance used for interacting with the data representation.
 */
class CodecSerialization<O>(val ops: DynamicOps<O>) : SerializationStrategy<ValueOutput, ValueInput> {
    /**
     * Encodes a given value into the target data representation using its associated `Codec`.
     *
     * @param D The type of the value to encode.
     * @param value The instance of the value to encode.
     * @param valueType The [kotlin.reflect.KType] of the value, used to retrieve the appropriate `Codec`.
     * @return The encoded value in the target data representation [T].
     * @throws IllegalStateException if the encoding fails.
     */
    override fun <D : Any> encode(output: ValueOutput, value: D, valueType: KType) {
        output.store("value", valueType.codec<D>(), value)
    }


    /**
     * Decodes data from the target data representation into a Kotlin object using its associated `Codec`.
     *
     * @param D The type of the value to decode.
     * @param data The data in the target representation [T] to decode.
     * @param oldValue An optional old value (unused in this stateless implementation).
     * @param valueType The [KType] of the value, used to retrieve the appropriate `Codec`.
     * @return A [io.kito.kore.common.data.DecodeResult.Stateless] containing the decoded value.
     * @throws IllegalStateException if the decoding fails.
     */
    override fun <D : Any> decode(data: ValueInput, oldValue: D?, valueType: KType): DecodeResult<D> =
        DecodeResult.Stateless(data.read("value", valueType.codec<D>()).orElseThrow())
}