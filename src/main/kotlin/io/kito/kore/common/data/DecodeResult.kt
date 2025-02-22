package io.kito.kore.common.data

sealed interface DecodeResult<T> {
    @JvmInline
    value class Stateless<T>(val value: T) : DecodeResult<T>

    @JvmInline
    value class Statefull<T>(val action: T.() -> Unit) : DecodeResult<T>
}