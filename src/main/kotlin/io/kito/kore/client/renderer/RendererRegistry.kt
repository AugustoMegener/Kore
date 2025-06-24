package io.kito.kore.client.renderer

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.client.event.EntityRenderersEvent

/**
 * A central registry for managing and registering custom entity and block entity renderers in Kore.
 * Annotated with `@Scan` to be automatically discovered by Kore for renderer registration.
 * This object collects renderer suppliers and registers them with NeoForge during the appropriate event.
 */
@Scan
object RendererRegistry {

    /**
     * A list of pairs, where each pair consists of a supplier for an [EntityType] and a supplier for its corresponding [EntityRenderer].
     * Renderers added to this list will be registered with NeoForge.
     */
    val entityRenderers =
        arrayListOf<Pair<() -> EntityType<out Entity>, (EntityRendererProvider.Context) -> EntityRenderer<out Entity>>>()

    /**
     * A list of pairs, where each pair consists of a supplier for a [BlockEntityType] and its corresponding [BlockEntityRenderer].
     * Renderers added to this list will be registered with NeoForge.
     */
    val blockEntityRenderers =
        arrayListOf<Pair<() -> BlockEntityType<out BlockEntity>, BlockEntityRenderer<out BlockEntity>>>()


    /**
     * Event subscriber method that registers all collected entity and block entity renderers with NeoForge.
     * Annotated with `@KSubscribe` to be automatically invoked by Kore's event system.
     * This method listens for the [EntityRenderersEvent.RegisterRenderers] event.
     *
     * @param event The [EntityRenderersEvent.RegisterRenderers] event, provided by NeoForge.
     */
    @KSubscribe
    @Suppress(UNCHECKED_CAST)
    fun EntityRenderersEvent.RegisterRenderers.onRegisterRenderers() {
        // Register all collected entity renderers
        entityRenderers.forEach { (et, er) ->
            registerEntityRenderer(et()) { er(it) as EntityRenderer<Entity> }
        }
        // Register all collected block entity renderers
        blockEntityRenderers.forEach { (bet, ber) ->
            registerBlockEntityRenderer(bet()) { ber as BlockEntityRenderer<BlockEntity> }
        }
    }
}

