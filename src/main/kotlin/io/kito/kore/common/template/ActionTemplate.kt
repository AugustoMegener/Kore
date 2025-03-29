package io.kito.kore.common.template

class ActionTemplate<T>(val action: (T) -> Unit) : Template<T, Unit> {
    override val allIdxs = arrayListOf<T>()

    override fun register(vararg idxs: T) {
        allIdxs += idxs
        idxs.forEach { action(it) }
    }

    override fun get(idx: T) = Unit
}