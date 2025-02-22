package io.kito.kore.common.data

import com.mojang.serialization.DynamicOps
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import kotlin.reflect.KType

class CodecSerialization<T : Any>(val ops: DynamicOps<T>) : SerializationStrategy<T> {
    override fun <D : Any> encode(value: D, valueType: KType): T =
        valueType.codec<D>().encodeStart(ops, value).orThrow

    override fun <D : Any> decode(data: T, oldValue: D?, valueType: KType) =
        DecodeResult.Stateless(valueType.codec<D>().parse(ops, data).orThrow)
}