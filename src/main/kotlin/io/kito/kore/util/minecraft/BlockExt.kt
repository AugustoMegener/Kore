package io.kito.kore.util.minecraft

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property

object BlockExt {

    class DefaultStateSetter(var state: BlockState) {
        infix fun <T : Comparable<T>, V: T, P: Property<T>> P.are(value: V) {
            state = state.setValue<T, V>(this, value)
        }
    }

    fun Block.stateOf(setter: DefaultStateSetter.() -> Unit) {
        DefaultStateSetter(stateDefinition.any()).apply(setter).state
    }

    operator fun <T : Comparable<T>, P: Property<T>> BlockState.get(i: P): T = getValue(i)
    operator fun <T : Comparable<T>, V: T, P: Property<T>> BlockState.set(i: P, value: V)
        { setValue(i, value) }
}