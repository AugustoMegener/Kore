package io.kito.kore.common.datagen

import net.minecraft.core.BlockPos
import net.minecraft.data.PackOutput
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType

class DynamicBlockStateProvider(private val entries : (RandomSource, BlockPos) -> BlockState)
    : BlockStateProvider()
{
    override fun type(): BlockStateProviderType<*> {
        TODO("Not yet implemented")
    }

    override fun getState(random: RandomSource, pos: BlockPos) = entries(random, pos)
}