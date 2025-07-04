package com.cosmic_jewelry.common.core.preset


import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock

open class BlockPreset<V, T : Block>(val preset: BlockBuilder<T>.(V) -> Unit,
                                     parents: Array<out Preset<V, BlockBuilder<T>>> = arrayOf()) :
    Preset<V, BlockBuilder<T>>(parents)
{
    override fun BlockBuilder<T>.action(value: V) { preset(value) }

    companion object {
        fun <T, B : Block> DataGenHelper.defaultStatePreset() =
            BlockPreset<T, B>({ defaultState() })
        
        fun <T, B : RotatedPillarBlock> DataGenHelper.logStatePreset() =
            BlockPreset<T, B>({ state { _, b -> logBlock(b) } })
    }
}