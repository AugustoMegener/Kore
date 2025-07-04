package io.kito.kore.common.datagen

import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootTable
import java.util.function.BiConsumer

class KLootTableSubProvider(val block: (BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) -> Unit)
    : LootTableSubProvider
{
    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) { block(output) }
}