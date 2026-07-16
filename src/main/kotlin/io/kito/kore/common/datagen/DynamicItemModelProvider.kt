package io.kito.kore.common.datagen

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.core.registries.BuiltInRegistries.ITEM
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.stream.Stream
import kotlin.collections.map
import kotlin.jvm.optionals.getOrNull

class DynamicItemModelProvider(
    modId: String,
    output: PackOutput,
    private val blockGenerators: List<Pair<ResourceLocation, BlockModelGenerators.(Block) -> Unit>>,
    private val itemGenerators: List<Pair<ResourceLocation, ItemModelGenerators.(Item) -> Unit>>
) : ModelProvider(output, modId)
{
    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
        blockGenerators.forEach { (loc, gen) -> blockModels.gen(BLOCK[loc].get().value()) }
        itemGenerators.forEach { (loc, gen) -> itemModels.gen(ITEM[loc].get().value()) }
    }

    override fun getKnownBlocks(): Stream<Holder.Reference<Block>> =
        blockGenerators.stream().map { BLOCK.get(it.first).orElseThrow() }
    override fun getKnownItems(): Stream<Holder.Reference<Item>?> =
        (itemGenerators.map {it.first } + blockGenerators.map {it.first })
            .stream().map { ITEM.get(it).getOrNull() }.filter { it != null }
}