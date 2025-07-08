package io.kito.kore.common.registry

import com.mojang.serialization.MapCodec
import io.kito.kore.Kore.ID
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.world.TagBiomeModifier
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.registries.NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object BiomeModifierSerializerRegistry : SimpleRegister<MapCodec<out BiomeModifier>>(ID, BIOME_MODIFIER_SERIALIZERS) {

    val tagBiomeModifierCodec: MapCodec<TagBiomeModifier> by "tag_biome_modifier"(TagBiomeModifier::mapCodec)
}