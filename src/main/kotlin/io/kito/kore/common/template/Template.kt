package io.kito.kore.common.template

interface Template<I, T> {

    val allIdxs: Collection<I>
    val registereds: Collection<() -> T> get() = allIdxs.map { { get(it)!! } }

    operator fun get(idx: I): T?

    operator fun contains(idx: I) = idx in allIdxs

    fun register(vararg idxs: I)

    companion object {
        fun <I, T, R : Template<I, T>> R.on(vararg idxs: I): R = also { register(*idxs) }
    }
}