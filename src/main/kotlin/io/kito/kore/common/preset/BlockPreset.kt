package io.kito.kore.common.preset


import com.cosmic_jewelry.common.core.preset.Preset
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import net.minecraft.client.data.models.model.TexturedModel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.levelgen.SurfaceRules.state

open class BlockPreset<V, T : Block>(val preset: BlockBuilder<T>.(V) -> Unit,
                                     parents: Array<out Preset<V, BlockBuilder<T>>> = arrayOf()) :
    Preset<V, BlockBuilder<T>>(parents)
{
    override fun BlockBuilder<T>.action(value: V) { preset(value) }

    companion object {
        fun <T, B : Block> DataGenHelper.cubeAllStatePreset() =
            BlockPreset<T, B>({ cubeAllModel() })
        
        fun <T, B : RotatedPillarBlock> DataGenHelper.logStatePreset(provider: TexturedModel.Provider) =
            BlockPreset<T, B>({ logModel(provider) })
    }
}