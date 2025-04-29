package io.kito.kore.common.template

import io.kito.kore.util.Indexable

interface Template<I, T> : Indexable<I, T?> {

    val allIdxs: Collection<I>
    val registereds: Collection<() -> T> get() = allIdxs.map { { get(it)!! } }

    operator fun contains(idx: I) = idx in allIdxs

    fun register(vararg idxs: I)

    companion object {
        fun <I, T, R : Template<I, T>> R.on(vararg idxs: I): R = also { register(*idxs) }
    }
}