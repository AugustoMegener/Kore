package io.kito.kore.util

interface Indexable<I, T> {
    operator fun get(idx: I): T
}