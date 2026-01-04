package io.kito.kore_tests.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.kito.kore.client.renderer.ModelLayer
import io.kito.kore.common.reflect.Scan
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState

class KMobModel(root: ModelPart) : EntityModel<EntityRenderState>(root) {

    private val bbMain: ModelPart = root.getChild("bb_main")

    constructor(ctx: EntityRendererProvider.Context) : this(ctx.bakeLayer(layerLocation))



    @Scan
    companion object : ModelLayer {

        override val layerLocation = ModelLayerLocation(local("custom_model"), "main")

        override fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }
}