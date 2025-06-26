package io.kito.kore.client.renderer

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.neoforged.neoforgespi.language.IModInfo

/**
 * Interface for defining custom model layers in Kore.
 * Implementations of this interface provide the necessary information to addEntry a model layer
 * with Minecraft, allowing custom 3D models to be used for entities, blocks, or items.
 */
interface ModelLayer {

    /**
     * The [ModelLayerLocation] that uniquely identifies this model layer.
     * This location is used by Minecraft to retrieve the model definition.
     */
    val layerLocation: ModelLayerLocation

    /**
     * Creates and returns the [LayerDefinition] for this model layer.
     * This function defines the structure and geometry of the 3D model.
     * @return A [LayerDefinition] instance representing the model.
     */
    fun createBodyLayer(): LayerDefinition

    /**
     * Companion object responsible for scanning and registering model layers.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {

        /**
         * Scans for objects that implement [ModelLayer] and registers their layer definitions with NeoForge.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [ModelLayer] instance to be registered.
         */
        @ObjectScanner(ModelLayer::class, 2)
        fun collectModelLayers(info: IModInfo, container: ModContainer, data: ModelLayer) {
            container.eventBus?.addListener { event: RegisterLayerDefinitions ->
                event.registerLayerDefinition(data.layerLocation, data::createBodyLayer)
            }
        }
    }
}

