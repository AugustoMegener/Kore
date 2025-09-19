package io.kito.kore.util.minecraft

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property

/**
 * Utility object providing extension functions and classes for Minecraft [Block]s and [BlockState]s.
 */
object BlockExt {

    /**
     * A helper class for setting default [BlockState] properties.
     * @property state The [BlockState] being modified.
     */
    class DefaultStateSetter(var state: BlockState) {
        /**
         * Infix function to set a property of the [BlockState] to a specific value.
         * @param T The type of the comparable value.
         * @param V The type of the value, a subtype of [T].
         * @param P The type of the [Property].
         * @param value The value to set the property to.
         */
        infix fun <T : Comparable<T>, V: T, P: Property<T>> P.are(value: V) {
            state = state.setValue<T, V>(this, value)
        }
    }

    /**
     * Extension function for [Block] to create a new [BlockState] with specified default properties.
     * @param setter A lambda that takes a [DefaultStateSetter] and applies property changes.
     * @return The modified [BlockState].
     */
    fun Block.stateOf(setter: DefaultStateSetter.() -> Unit) =
        DefaultStateSetter(stateDefinition.any()).apply(setter).state

    /**
     * Operator function to get the value of a [Property] from a [BlockState].
     * @param T The type of the comparable value.
     * @param P The type of the [Property].
     * @param i The [Property] to get the value from.
     * @return The value of the property.
     */
    operator fun <T : Comparable<T>, P: Property<T>> BlockState.get(i: P): T = getValue(i)
    /**
     * Operator function to set the value of a [Property] in a [BlockState].
     * @param T The type of the comparable value.
     * @param V The type of the value, a subtype of [T].
     * @param P The type of the [Property].
     * @param i The [Property] to set the value for.
     * @param value The value to set.
     */
    operator fun <T : Comparable<T>, V: T, P: Property<T>> BlockState.set(i: P, value: V)
        { setValue(i, value) }
}

