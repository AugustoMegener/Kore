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

open class KSimpleClientFluidTypeExt : IClientFluidTypeExtensions {

    var color = 0xFFFFFFF
    lateinit var stillTexturePath: ResourceLocation
    lateinit var flowingTexturePath: ResourceLocation

    override fun getTintColor() = color
    override fun getStillTexture() = stillTexturePath
    override fun getFlowingTexture() = flowingTexturePath


    companion object {

        private val fluidTypeExts = hashMapOf<String, ArrayList<Pair<() -> Holder<FluidType>, IClientFluidTypeExtensions>>>()

        fun FluidTypeBuilder.client(block: KSimpleClientFluidTypeExt.() -> Unit) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to KSimpleClientFluidTypeExt().apply(block)
        }

        fun <T: IClientFluidTypeExtensions> FluidTypeBuilder.client(ext: T) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to ext
        }

        fun <T: IClientFluidTypeExtensions> FluidTypeBuilder.client(ext: T, block: T.() -> Unit) {
            fluidTypeExts.computeIfAbsent(id) { arrayListOf() } +=
                { fluidTypeRegistry } to ext.apply(block)
        }

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