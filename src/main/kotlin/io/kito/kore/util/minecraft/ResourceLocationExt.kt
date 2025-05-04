package io.kito.kore.util.minecraft

import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.resources.ResourceLocation.parse
import net.minecraft.resources.ResourceLocation.withDefaultNamespace

object ResourceLocationExt {

    fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
    fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

    fun String.toLoc() = parse(this)

    inline val ResourceLocation.entity: ResourceLocation get() = withPrefix("entity/")

    inline val ResourceLocation.item: ResourceLocation get() = withPrefix("item/")

    inline val ResourceLocation.block: ResourceLocation get() = withPrefix("block/")

    inline val ResourceLocation.texture: ResourceLocation get() = withPrefix("textures/")
}