package io.kito.kore.client.renderer

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.client.event.EntityRenderersEvent

@Scan
object RendererRegistry {

    val entityRenderers =
        arrayListOf<Pair<() -> EntityType<out Entity>, (EntityRendererProvider.Context) -> EntityRenderer<out Entity>>>()

    val blockEntityRenderers =
        arrayListOf<Pair<() -> BlockEntityType<out BlockEntity>, BlockEntityRenderer<out BlockEntity>>>()


    @KSubscribe
    @Suppress(UNCHECKED_CAST)
    fun EntityRenderersEvent.RegisterRenderers.onRegisterRenderers() {
        entityRenderers.forEach { (et, er) ->
            registerEntityRenderer(et()) { er(it) as EntityRenderer<Entity> }
        }
        blockEntityRenderers.forEach { (bet, ber) ->
            registerBlockEntityRenderer(bet()) { ber as BlockEntityRenderer<BlockEntity> }
        }
    }
}
