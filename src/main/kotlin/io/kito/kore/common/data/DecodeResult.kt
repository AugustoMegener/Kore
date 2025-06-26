package io.kito.kore.common.data

/**
 * A sealed interface representing the result of a decoding operation.
 * It provides two distinct ways to handle the decoded data: stateless or stateful.
 *
 * @param T The type of the decoded value.
 */
sealed interface DecodeResult<T> {
    /**
     * Represents a stateless decoding result, where the decoded value is directly available.
     * This is typically used when the decoding process does not require modifying an existing object
     * or when the decoded data is a new instance.
     *
     * @param value The decoded value of type [T].
     */
    @JvmInline
    value class Stateless<T>(val value: T) : DecodeResult<T>

    /**
     * Represents a stateful decoding result, where the decoded data is applied as an action
     * to an existing object of type [T]. This is useful for updating an object's state
     * rather than creating a new one.
     *
     * @param action A lambda function that takes an instance of [T] as its receiver
     *               and applies the decoded changes to it.
     */
    @JvmInline
    value class Statefull<T>(val action: T.() -> Unit) : DecodeResult<T>
}

