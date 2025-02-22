package io.kito.kore.common.data.nbt

import io.kito.kore.common.data.DecodeResult.Statefull
import io.kito.kore.common.data.DecodeResult.Stateless
import io.kito.kore.common.data.NBTSerialization
import io.kito.kore.common.data.Save.Companion.saveFields
import io.kito.kore.common.data.SerializationStrategy
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import org.jetbrains.annotations.UnknownNullability
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

interface KNBTSerializable : INBTSerializable<CompoundTag> {

    val serializableProperties: List<Pair<String, KProperty1<KNBTSerializable, Any>>>
        get() = saveFields

    val strategy: (Provider) -> SerializationStrategy<Tag> get() = { NBTSerialization(it) }

    override fun serializeNBT(provider: Provider): @UnknownNullability CompoundTag =
         CompoundTag().also {
             val serializer = strategy(provider)

             serializableProperties.forEach { (name, field) ->
                 it.put(name, serializer.encode(field.get(this), field.returnType))
             }
         }

    override fun deserializeNBT(provider: Provider, nbt: CompoundTag) {
        val serializer = strategy(provider)

        serializableProperties.forEach { (name, field) ->
            nbt[name]?.let { tag ->
                when (val result = serializer.decode(tag, field.get(this), field.returnType)) {
                    is Stateless -> (field as? KMutableProperty1<KNBTSerializable, Any>)?.set(this, result.value)
                    is Statefull -> field.get(this).apply(result.action)
                }
            }
        }
    }
}