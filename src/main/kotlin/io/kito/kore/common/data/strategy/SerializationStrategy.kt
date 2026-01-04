package io.kito.kore.common.data.strategy

import io.kito.kore.common.data.DecodeResult
import kotlin.reflect.KType

interface SerializationStrategy<N, C> {
    fun <D : Any> encode(output: N, value: D, valueType: KType)
    fun <D : Any> decode(data: C, oldValue: D?, valueType: KType) : DecodeResult<D>
}