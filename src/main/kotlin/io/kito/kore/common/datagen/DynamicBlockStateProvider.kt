package io.kito.kore.common.datagen

import net.minecraft.core.BlockPos
import net.minecraft.data.PackOutput
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType

/**
 * A dynamic [BlockStateProvider] that allows for registering block states and models
 * using a list of lambda functions. This provides flexibility in defining block states
 * programmatically during data generation.
 *
 * @param output The [PackOutput] for writing generated data.
 * @param modid The mod ID for which data is being generated.
 * @param existingFileHelper An [ExistingFileHelper] to check for existing files.
 * @param entries A list of lambda functions, each taking a [DynamicBlockStateProvider] instance
 *                and applying block state and model definitions to it. These lambdas encapsulate
 *                the logic for generating specific block states.
 */
class DynamicBlockStateProvider(private val entries : (RandomSource, BlockPos) -> BlockState)
    : BlockStateProvider()
{
    override fun type(): BlockStateProviderType<*> {
        TODO("Not yet implemented")
    }

    override fun getState(random: RandomSource, pos: BlockPos) = entries(random, pos)
}