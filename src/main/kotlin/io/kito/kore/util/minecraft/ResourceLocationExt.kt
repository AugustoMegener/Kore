package io.kito.kore.util.minecraft

import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.*

/**
 * Utility object providing extension functions for [ResourceLocation]s.
 * These functions simplify the creation and manipulation of resource locations in Minecraft.
 */
object ResourceLocationExt {

    /**
     * Creates a [ResourceLocation] with the default namespace.
     * @param name The path of the resource location.
     * @return A new [ResourceLocation] instance.
     */
    fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
    /**
     * Creates a [ResourceLocation] with a specified namespace and path.
     * @param id The namespace of the resource location (typically the mod ID).
     * @param name The path of the resource location.
     * @return A new [ResourceLocation] instance.
     */
    fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

    /**
     * Extension function to parse a [String] into a [ResourceLocation].
     * @return A new [ResourceLocation] instance parsed from the string.
     */
    fun String.toLoc() = parse(this)

    /**
     * Extension property to add an "entity/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.entity: ResourceLocation get() = withPrefix("entity/")

    /**
     * Extension property to add an "item/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.item: ResourceLocation get() = withPrefix("item/")

    /**
     * Extension property to add a "block/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.block: ResourceLocation get() = withPrefix("block/")

    /**
     * Extension property to add a "gui/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.gui: ResourceLocation get() = withPrefix("gui/")

    /**
     * Extension property to add a "container/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.container: ResourceLocation get() = withPrefix("container/")

    /**
     * Extension property to add a "textures/" prefix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.texture: ResourceLocation get() = withPrefix("textures/")

    /**
     * Extension property to add a ".png" suffix to the path of a [ResourceLocation].
     */
    inline val ResourceLocation.png: ResourceLocation get() = withSuffix(".png")
}

