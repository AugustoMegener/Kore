package io.kito.kore.common.template

/**
 * A template class for performing actions on multiple items based on an index.
 * This class implements the [Template] interface, but instead of registering objects,
 * it executes a given action for each registered index.
 *
 * @param T The type of the index used to identify individual items for which the action will be performed.
 * @property action The lambda function that defines the action to be performed for each index.
 */
class ActionTemplate<T>(val action: (T) -> Unit) : Template<T, Unit> {
    /**
     * A list to store all registered indices.
     */
    override val allIdxs = arrayListOf<T>()

    /**
     * Registers indices and performs the defined action for each of them.
     * @param idxs A vararg of indices for which the action will be executed.
     */
    override fun register(vararg idxs: T) {
        allIdxs += idxs
        idxs.forEach { action(it) }
    }

    /**
     * This method is not applicable for [ActionTemplate] as it does not return a registered object.
     * It always returns [Unit].
     * @param idx The index (ignored).
     * @return [Unit]
     */
    override fun get(idx: T) = Unit
}

