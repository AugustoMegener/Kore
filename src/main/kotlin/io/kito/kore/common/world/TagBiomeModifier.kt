package io.kito.kore.common.world

import com.mojang.serialization.MapCodec
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.KMapCodecSerializer
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep.Decoration
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo

data class TagBiomeModifier(@Save val tag      : TagKey<Biome>,
                            @Save val features : HolderSet<PlacedFeature>,
                            @Save val step     : Decoration) : BiomeModifier
{
    override fun modify(biome   : Holder<Biome>,
                        phase   : BiomeModifier.Phase,
                        builder : ModifiableBiomeInfo.BiomeInfo.Builder)
    {
        if (!(phase == BiomeModifier.Phase.ADD && tag in biome.tags().toList())) return

        features.forEach { builder.generationSettings.addFeature(step, it) }
    }

    override fun codec() = TagBiomeModifier.mapCodec

    companion object : KMapCodecSerializer<TagBiomeModifier>(TagBiomeModifier::class)
}