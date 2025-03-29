package io.kito.kore_tests.client.renderer

import io.kito.kore.util.neoforge.ResourceLocationExt.entity
import io.kito.kore.util.neoforge.ResourceLocationExt.texture
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class KMobRenderer(ctx: EntityRendererProvider.Context) : MobRenderer<KMob, KMobModel>(ctx, KMobModel(ctx), 0f) {

    override fun getTextureLocation(entity: KMob) = local("kmob.png").entity.texture
}