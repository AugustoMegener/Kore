package io.kito.kore.util.minecraft

import net.minecraft.world.level.levelgen.placement.*

object PlacedFeatureExt {

    fun orePlacement(pCountPlacement: PlacementModifier, pHeightRange: PlacementModifier) =
        listOf(pCountPlacement, InSquarePlacement.spread(), pHeightRange, BiomeFilter.biome())

    fun commonOrePlacement(pCount: Int, pHeightRange: PlacementModifier) =
        orePlacement(CountPlacement.of(pCount), pHeightRange)

    fun rareOrePlacement(pChance: Int, pHeightRange: PlacementModifier) =
        orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange)
}