package io.kito.kore_tests.client.renderer

import io.kito.kore.util.minecraft.ResourceLocationExt.entity
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.resources.ResourceLocation

class Foo : EntityRenderState()

class KMobRenderer(ctx: EntityRendererProvider.Context) :
    MobRenderer<KMob, LivingEntityRenderState, KMobModel>(ctx, KMobModel(ctx), 0f)
{
    override fun createRenderState() = LivingEntityRenderState()

    override fun getTextureLocation(renderState: LivingEntityRenderState) = local("kmob.png").entity.texture
}