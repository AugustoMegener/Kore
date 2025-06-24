package io.kito.kore.util

import io.kito.kore.util.Bound.Type.GLOBAL
import io.kito.kore.util.Bound.Type.LOCAL

/**
 * Represents a binding scope, either global or local to a specific mod ID.
 * This sealed class provides a way to define how certain functionalities or registrations
 * are scoped within the Kore framework.
 */
sealed class Bound {

    /**
     * Defines the type of binding scope.
     */
    enum class Type { GLOBAL, LOCAL }

    /**
     * Represents a global binding scope, applicable across all mod IDs.
     */
    data object Global                 : Bound()
    /**
     * Represents a local binding scope, tied to a specific mod ID.
     * @property id The mod ID to which this local bound is associated.
     */
    data  class  Local(val id: String) : Bound()

    /**
     * Checks if this bound is active for a given mod ID.
     * @param modId The mod ID to check against.
     * @return `true` if the bound is global or matches the given mod ID, `false` otherwise.
     */
    fun isOnBound(modId: String) = when (this) { is Global -> true; is Local -> id == modId }

    /**
     * Companion object for creating [Bound] instances.
     */
    companion object {
        /**
         * Creates a [Bound] instance based on the specified [Type] and an optional mod ID.
         * @param bound The type of bound to create ([GLOBAL] or [LOCAL]).
         * @param id The mod ID to use if the bound type is [LOCAL]. Ignored for [GLOBAL].
         * @return A [Bound.Global] instance if [bound] is [GLOBAL], or a [Bound.Local] instance if [bound] is [LOCAL].
         */
        operator fun invoke(bound: Type, id: String) = when(bound) { GLOBAL -> Global; LOCAL -> Local(id) }
    }
}

