package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.storage.loot.LootTable


class KBlockLootTableSubProvider(lookup: HolderLookup.Provider,
                                 val block: BlockLootSubProvider.(HolderLookup.Provider) -> Unit,
                                 items: Set<Item> = setOf(),
                                 featureFlags: FeatureFlagSet = FeatureFlags.REGISTRY.allFlags(),
                                 builders: Map<ResourceKey<LootTable>, LootTable.Builder> = mapOf()) :
    BlockLootSubProvider(items, featureFlags, builders, lookup)
{
    override fun generate() { block(registries) }
}

