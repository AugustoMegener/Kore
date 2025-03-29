package io.kito.kore.util.neoforge

import net.minecraft.resources.ResourceLocation

object ResourceLocationExt {

    inline val ResourceLocation.entity: ResourceLocation get() = withPrefix("entity/")

    inline val ResourceLocation.item: ResourceLocation get() = withPrefix("item/")

    inline val ResourceLocation.texture: ResourceLocation get() = withPrefix("textures/")
}