package io.kito.kore.common.data.nbt

import io.kito.kore.common.data.DecodeResult.Statefull
import io.kito.kore.common.data.DecodeResult.Stateless
import io.kito.kore.common.data.strategy.NBTSerialization
import io.kito.kore.common.data.Save.Companion.saveFields
import io.kito.kore.common.data.strategy.SerializationStrategy
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.neoforged.neoforge.common.util.ValueIOSerializable
import org.jetbrains.annotations.UnknownNullability
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

interface KValueIOSerializable : ValueIOSerializable{

    val serializableProperties: List<Pair<String, KProperty1<KValueIOSerializable, Any>>>
        get() = saveFields

    val strategy: SerializationStrategy<ValueOutput, ValueInput>

    override fun serialize(output: ValueOutput) {
        serializableProperties.forEach { (name, field) ->
            strategy.encode(output.child(name), field.get(this), field.returnType)

        }
    }

    override fun deserialize(input: ValueInput) {
        serializableProperties.forEach { (name, field) ->
            input.child(name).getOrNull()?.let {
                when (val result = strategy.decode(it, field.get(this), field.returnType)) {
                    is Stateless -> (field as? KMutableProperty1<KValueIOSerializable, Any>)?.set(this, result.value)
                    is Statefull -> field.get(this).apply(result.action)
                }
            }
        }
    }
}