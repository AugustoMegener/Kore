package io.kito.kore.util.minecraft

import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.*


object ResourceLocationExt {

    val ResourceLocation.entity     : ResourceLocation get() = withPrefix("entity/"   )
    val ResourceLocation.item       : ResourceLocation get() = withPrefix("item/"     )
    val ResourceLocation.block      : ResourceLocation get() = withPrefix("block/"    )
    val ResourceLocation.gui        : ResourceLocation get() = withPrefix("gui/"      )
    val ResourceLocation.sprite     : ResourceLocation get() = withPrefix("sprite/"   )
    val ResourceLocation.container  : ResourceLocation get() = withPrefix("container/")
    val ResourceLocation.texture    : ResourceLocation get() = withPrefix("textures/" )

    val ResourceLocation.png        : ResourceLocation get() = withSuffix(".png"      )

    fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
    fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

    fun String.toLoc(): ResourceLocation = parse(this)

    operator fun ResourceLocation. plus(suffix: String) : ResourceLocation = withSuffix(suffix)
    infix fun String.on(loc: ResourceLocation) : ResourceLocation = loc.withPrefix(this)
}

