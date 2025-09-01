package io.kito.kore.common.data

import io.kito.kore.common.data.DecodeResult.Statefull
import io.kito.kore.common.data.DecodeResult.Stateless
import io.kito.kore.common.data.nbt.NBTStatefullSerializer
import io.kito.kore.common.data.nbt.NBTStatelessSerializer
import io.kito.kore.common.data.nbt.RegisterNBTSerializable.Companion.nbtSerializer
import io.kito.kore.common.data.nbt.RegisterNBTSerializable.Companion.nbtSerializerRegistry
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.nbtOps
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import kotlin.reflect.KType

/**
 * An implementation of [SerializationStrategy] that handles serialization and deserialization
 * of data to and from Minecraft's NBT (Named Binary Tag) format.
 * It supports various data types, including [INBTSerializable] objects, [ArrayList], [NonNullList],
 * and other types via Mojang's `Codec` system.
 *
 * @param provider The [HolderLookup.Provider] used for NBT serialization/deserialization, providing access to registries.
 */
class NBTSerialization(val provider: Provider) : SerializationStrategy<Tag> {

    /**
     * An internal [CodecSerialization] instance specifically for NBT operations.
     * This is used as a fallback for types not explicitly handled by this class.
     */
    private val nbtCodecSerializer = CodecSerialization(nbtOps)

    /**
     * Encodes a given value into an NBT [Tag].
     * It handles different types specifically:
     * - If the value is [INBTSerializable], it uses its `serializeNBT` method.
     * - If the value is an [ArrayList] or [NonNullList], it serializes each element into a [ListTag].
     * - For all other types, it delegates to the internal [nbtCodecSerializer].
     *
     * @param D The type of the value to encode.
     * @param value The instance of the value to encode.
     * @param valueType The [KType] of the value, used for generic type information, especially for lists.
     * @return The encoded value as an NBT [Tag].
     */
    override fun <D : Any> encode(value: D, valueType: KType): Tag =
        when(value) {
            is INBTSerializable<*> -> value.serializeNBT(provider)
            is ArrayList<*>,
            is NonNullList<*> ->
                ListTag().also { lst ->
                    lst.addAll((value as List<*>).map { i -> i?.let { encode(it, valueType.arguments.first().type!!) } })
                }
            else -> when {
                value::class in nbtSerializerRegistry.keys -> value.nbtSerializer!!.serialize(value, provider)
                else -> nbtCodecSerializer.encode(value, valueType)
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
     * @return A [DecodeResult] containing the decoded value or an action to apply the decoded data.
     */
    @Suppress(UNCHECKED_CAST)
    override fun <D : Any> decode(data: Tag, oldValue: D?, valueType: KType): DecodeResult<D> =
        when(oldValue) {
            is INBTSerializable<*> -> Statefull { (this as INBTSerializable<Tag>); deserializeNBT(provider, data) }

            is ArrayList<*> -> Statefull { this as ArrayList<Any>
                val new = (data as ListTag)
                    .mapIndexed { i, it -> decode(it, getOrNull(i), valueType.arguments.first().type!!) }

                clear()

                new.forEachIndexed { i, result ->
                    when (result) {
                        is Statefull -> throw IllegalStateException(
                            "it is not possible to deserialize a mutable list from statefull items, you should " +
                            "implement your own solution"
                        )

                        is Stateless -> add(result.value)
                    }
                }
            }

            is NonNullList<*> ->
                (data as ListTag).mapIndexed { i, it ->
                    decode(it, (oldValue as NonNullList<Any>).getOrNull(i), valueType.arguments.first().type!!)
                }.let {
                    when (it.first()) {
                        is Statefull -> Statefull { this as NonNullList<Any>
                            (it as List<Statefull<Any>>).forEachIndexed { i, v -> v.action(get(i)) }
                        }

                        is Stateless -> Stateless(NonNullList.copyOf((it as List<Stateless<*>>).map { i -> i.value }) as D)
                    }
                }

            else -> when {
                oldValue?.let { it::class in nbtSerializerRegistry.keys } == true ->
                    when(val serializer = oldValue.nbtSerializer!!) {
                        is NBTStatefullSerializer ->
                            Statefull { serializer.deserialize(provider, data as CompoundTag, this) }
                        is NBTStatelessSerializer ->
                            Stateless(serializer.deserialize(provider, data as CompoundTag))
                    }
                else -> nbtCodecSerializer.decode(data, oldValue, valueType)
            }
        }

}

