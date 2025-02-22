package io.kito.kore.common.data

import io.kito.kore.common.data.DecodeResult.Statefull
import io.kito.kore.common.data.DecodeResult.Stateless
import io.kito.kore.util.nbtOps
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

class NBTSerialization(val provider: Provider) : SerializationStrategy<Tag> {

    private val nbtCodecSerializer = CodecSerialization(nbtOps)

    override fun <D : Any> encode(value: D, valueType: KType): Tag =
        when(value) {
            is INBTSerializable<*> -> value.serializeNBT(provider)
            is ArrayList<*>,
            is NonNullList<*> ->
                ListTag().also { lst ->
                    lst.addAll((value as List<*>).map { i -> i?.let { encode(it, valueType.arguments.first().type!!) } })
                }
            else -> nbtCodecSerializer.encode(value, valueType)
        }


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
                            "it is not possible to deserialize a mutable list of statefull items, you should " +
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

            else -> nbtCodecSerializer.decode(data, oldValue, valueType)
        }

}