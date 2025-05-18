package io.kito.kore.client.renderer

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.neoforged.neoforgespi.language.IModInfo

interface ModelLayer {

    val layerLocation: ModelLayerLocation

    fun createBodyLayer(): LayerDefinition

    @Scan
    companion object {

        @ObjectScanner(ModelLayer::class, 2)
        fun collectModelLayers(info: IModInfo, container: ModContainer, data: ModelLayer) {
            container.eventBus?.addListener { event: RegisterLayerDefinitions ->
                event.registerLayerDefinition(data.layerLocation, data::createBodyLayer)
            }
        }
    }
}