package io.kito.kore.client.renderer.ext

import io.kito.kore.common.registry.FluidTypeRegister.FluidTypeBuilder
import io.kito.kore.util.neoforge.Mods.forEachKoreUserFile
import io.kito.kore.util.neoforge.Mods.modContainer
import io.kito.kore.util.neoforge.Mods.modId
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.fluids.FluidType

/**
 * An open class providing a simple implementation of [IClientFluidTypeExtensions] for custom fluid rendering.
 * This class allows defining the color and textures for still and flowing fluid states.
 */
open class KSimpleClientFluidTypeExt : IClientFluidTypeExtensions {

    /**
     * The tint color of the fluid in ARGB format (Alpha, Red, Green, Blue).
     * Defaults to white (0xFFFFFFF).
     */
    var color = 0xFFFFFFF
    /**
     * The [ResourceLocation] of the texture for the still fluid state.
     * This must be initialized before use.
     */
    lateinit var stillTexturePath: ResourceLocation
    /**
     * The [ResourceLocation] of the texture for the flowing fluid state.
     * This must be initialized before use.
     */
    lateinit var flowingTexturePath: ResourceLocation

    /**
     * Returns the tint color of the fluid.
     * @return The ARGB color as an integer.
     */
    override fun getTintColor() = color
    /**
     * Returns the [ResourceLocation] of the still fluid texture.
     * @return The [ResourceLocation] for the still texture.
     */
    override fun getStillTexture() = stillTexturePath
    /**
     * Returns the [ResourceLocation] of the flowing fluid texture.
     * @return The [ResourceLocation] for the flowing texture.
     */
    override fun getFlowingTexture() = flowingTexturePath


    /**
     * Companion object providing extension functions for [FluidTypeBuilder] to easily configure
     * client-side fluid properties and a function to register these extensions.
     */
    companion object {

        /**
         * A map storing client fluid type extensions, keyed by mod ID.
         * Each entry contains a list of pairs: a supplier for a [FluidType] holder and its corresponding [IClientFluidTypeExtensions] instance.
         */
        private val fluidTypeExts = hashMapOf<String, ArrayList<Pair<() -> Holder<FluidType>, IClientFluidTypeExtensions>>>()

        /**
         * Extension function for [FluidTypeBuilder] to define client-side fluid properties using a lambda with [KSimpleClientFluidTypeExt].
         * This allows configuring color and textures directly within the fluid type definition.
         * @param block A lambda that configures the [KSimpleClientFluidTypeExt] instance.
         */
        fun FluidTypeBuilder.client(block: KSimpleClientFluidTypeExt.() -> Unit) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to KSimpleClientFluidTypeExt().apply(block)
        }

        /**
         * Extension function for [FluidTypeBuilder] to associate an existing [IClientFluidTypeExtensions] instance with a fluid type.
         * @param ext The [IClientFluidTypeExtensions] instance to associate.
         */
        fun <T: IClientFluidTypeExtensions> FluidTypeBuilder.client(ext: T) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to ext
        }

        /**
         * Extension function for [FluidTypeBuilder] to associate an existing [IClientFluidTypeExtensions] instance with a fluid type
         * and further configure it using a lambda.
         * @param ext The [IClientFluidTypeExtensions] instance to associate.
         * @param block A lambda that further configures the provided [IClientFluidTypeExtensions] instance.
         */
        fun <T: IClientFluidTypeExtensions> FluidTypeBuilder.client(ext: T, block: T.() -> Unit) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to ext.apply(block)
        }

        /**
         * Registers all collected client-side fluid type extensions with NeoForge.
         * This function iterates through all registered fluid type extensions for each mod
         * and adds them to the [RegisterClientExtensionsEvent] listener.
         */
        fun registerFluidTypeClientExts() {
            forEachKoreUserFile {
                modContainer.eventBus?.addListener<RegisterClientExtensionsEvent> {
                    fluidTypeExts[modId]?.forEach { (fts, cfte) ->
                        it.registerFluidType(cfte, fts())
                    }
                }
            }
        }
    }
}

