package io.kito.kore.common.data.strategy

import io.kito.kore.common.data.DecodeResult
import io.kito.kore.common.data.nbt.ValueStatefulSerializer
import io.kito.kore.common.data.nbt.ValueStatelessSerializer
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.nbtOps
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.util.ProblemReporter
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.neoforged.neoforge.common.util.ValueIOSerializable
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

/**
 * An implementation of [SerializationStrategy] that handles serialization and deserialization
 * of data to and from Minecraft's NBT (Named Binary Tag) format.
 * It supports various data types, including [INBTSerializable] objects, [ArrayList], [net.minecraft.core.NonNullList],
 * and other types via Mojang's `Codec` system.
 *
 * @param provider The [HolderLookup.Provider] used for NBT serialization/deserialization, providing access to registries.
 */
class NBTSerialization(val provider: HolderLookup.Provider) : SerializationStrategy<ValueOutput, ValueInput> {

    /**
     * An internal [CodecSerialization] instance specifically for NBT operations.
     * This is used as a fallback for types not explicitly handled by this class.
     */
    private val nbtCodecSerializer = CodecSerialization(nbtOps)

    /**
     * Encodes a given value into an NBT [Tag].
     * It handles different types specifically:
     * - If the value is [INBTSerializable], it uses its `serializeNBT` method.
     * - If the value is an [ArrayList] or [net.minecraft.core.NonNullList], it serializes each element into a [net.minecraft.nbt.ListTag].
     * - For all other types, it delegates to the internal [nbtCodecSerializer].
     *
     * @param D The type of the value to encode.
     * @param value The instance of the value to encode.
     * @param valueType The [kotlin.reflect.KType] of the value, used for generic type information, especially for lists.
     * @return The encoded value as an NBT [Tag].
     */
    override fun <D : Any> encode(output: ValueOutput, value: D, valueType: KType) {
        when (value) {
            is ValueIOSerializable ->
                TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider).also {
                    value.serialize(it)
                }.buildResult()

            is ArrayList<*>,
            is NonNullList<*> ->
                output.childrenList("value").run {
                    value.forEach { encode(addChild(), it, valueType.arguments[0].type!!) }
                }

            else -> nbtCodecSerializer.encode(output, value, valueType)

        }
    }


    /**
     * Decodes data from an NBT [Tag] into a Kotlin object.
     * It handles different types specifically:
     * - If `oldValue` is [INBTSerializable], it applies the decoded NBT to the existing object (stateful).
     * - If `oldValue` is an [ArrayList], it decodes elements into the existing list (stateful).
     * - If `oldValue` is a [NonNullList], it decodes elements into the existing list (stateful) or creates a new one (stateless).
     * - For all other types, it delegates to the internal [nbtCodecSerializer] (stateless).
     *
     * @param D The type of the value to decode.
     * @param data The NBT [Tag] to decode.
     * @param oldValue An optional existing object to decode into (for stateful decoding).
     * @param valueType The [KType] of the value, used for generic type information, especially for lists.
     * @return A [io.kito.kore.common.data.DecodeResult] containing the decoded value or an action to apply the decoded data.
     */
    @Suppress(UNCHECKED_CAST)
    override fun <D : Any> decode(data: ValueInput, oldValue: D?, valueType: KType): DecodeResult<D> =
        when(oldValue) {
            is ValueIOSerializable -> DecodeResult.Statefull {
                (this as ValueIOSerializable);
                deserialize(data)
            }

            is ArrayList<*> -> DecodeResult.Statefull {
                this as ArrayList<Any>
                val new = (data.childrenListOrEmpty("value")).toList().mapIndexed { i, it ->
                    decode(it, getOrNull(i), valueType.arguments.first().type!!)
                }

                clear()

                new.forEachIndexed { i, result ->
                    when (result) {
                        is DecodeResult.Statefull -> throw IllegalStateException(
                            "it is not possible to deserialize a mutable list from statefull items, you should " +
                                    "implement your own solution"
                        )

                        is DecodeResult.Stateless -> add(result.value)
                    }
                }
            }

            is NonNullList<*> ->
                (data.childrenListOrEmpty("value")).mapIndexed { i, it ->
                    decode(it, (oldValue as NonNullList<Any>).getOrNull(i), valueType.arguments.first().type!!)
                }.let {
                    when (it.first()) {
                        is DecodeResult.Statefull -> DecodeResult.Statefull {
                            this as NonNullList<Any>
                            (it as List<DecodeResult.Statefull<Any>>).forEachIndexed { i, v -> v.action(get(i)) }
                        }

                        is DecodeResult.Stateless -> DecodeResult.Stateless(
                            NonNullList.copyOf((it as List<DecodeResult.Stateless<*>>).map { i -> i.value }) as D
                        )
                    }
                }

            else  -> nbtCodecSerializer.decode(data, oldValue, valueType)
        }
}