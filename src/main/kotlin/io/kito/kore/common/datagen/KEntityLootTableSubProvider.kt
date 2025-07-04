package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.EntityLootSubProvider
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags


class KEntityLootTableSubProvider(lookup: HolderLookup.Provider,
                                  val block: EntityLootSubProvider.(HolderLookup.Provider) -> Unit,
                                  allowed: FeatureFlagSet = FeatureFlags.REGISTRY.allFlags(),
                                  required: FeatureFlagSet = FeatureFlagSet.of()) :
    EntityLootSubProvider(allowed, required, lookup)
{
    override fun generate() { block(registries) }
}

