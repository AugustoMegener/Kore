package io.kito.kore.common.data

import kotlin.reflect.KType

interface SerializationStrategy<T> {
    fun <D : Any> encode(value: D, valueType: KType) : T
    fun <D : Any> decode( data: T, oldValue: D?, valueType: KType) : DecodeResult<D>
}