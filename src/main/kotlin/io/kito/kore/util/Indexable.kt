package io.kito.kore.util

/**
 * A generic interface for types that can be indexed by a specific key.
 * This provides a common contract for accessing elements using an index.
 *
 * @param I The type of the index (e.g., [String], [Int], an enum).
 * @param T The type of the value that can be retrieved using the index.
 */
interface Indexable<I, T> {
    /**
     * Retrieves a value of type [T] using the given index [I].
     * @param idx The index to use for retrieval.
     * @return The value associated with the given index.
     */
    operator fun get(idx: I): T
}

