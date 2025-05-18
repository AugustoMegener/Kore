package io.kito.kore.util.minecraft

import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.*

object ResourceLocationExt {

    fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
    fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

    fun String.toLoc() = parse(this)

    inline val ResourceLocation.entity: ResourceLocation get() = withPrefix("entity/")

    inline val ResourceLocation.item: ResourceLocation get() = withPrefix("item/")

    inline val ResourceLocation.block: ResourceLocation get() = withPrefix("block/")

    inline val ResourceLocation.gui: ResourceLocation get() = withPrefix("gui/")

    inline val ResourceLocation.container: ResourceLocation get() = withPrefix("container/")

    inline val ResourceLocation.texture: ResourceLocation get() = withPrefix("textures/")

    inline val ResourceLocation.png: ResourceLocation get() = withSuffix(".png")
}