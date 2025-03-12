package io.kito.kore.util.neoforge

import net.minecraft.resources.ResourceLocation

object ResourceLocationExt {

    inline val ResourceLocation.entityTexture: ResourceLocation get() = withPrefix("textures/entity/")
}