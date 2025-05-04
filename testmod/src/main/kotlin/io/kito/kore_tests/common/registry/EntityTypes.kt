package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.EntityTypeRegister
import io.kito.kore.util.minecraft.MobExt.armor
import io.kito.kore.util.minecraft.MobExt.armorToughness
import io.kito.kore.util.minecraft.MobExt.followRange
import io.kito.kore.util.minecraft.MobExt.gravity
import io.kito.kore.util.minecraft.MobExt.knockbackResistance
import io.kito.kore.util.minecraft.MobExt.maxAbsorption
import io.kito.kore.util.minecraft.MobExt.maxHealth
import io.kito.kore.util.minecraft.MobExt.movementEfficiency
import io.kito.kore.util.minecraft.MobExt.movementSpeed
import io.kito.kore.util.minecraft.MobExt.scale
import io.kito.kore.util.minecraft.MobExt.stepHeight
import io.kito.kore_tests.DataGenerator.spawnEggModel
import io.kito.kore_tests.ID
import io.kito.kore_tests.client.renderer.KMobRenderer
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.world.entity.MobCategory.MISC
import net.minecraft.world.level.block.Blocks.WATER
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object EntityTypes : EntityTypeRegister(ID) {

    val myMobType by "mob" ofMob (::KMob to MISC) that {
        renderer(::KMobRenderer)

        spawnEgg(0xc76ded, 0x492e54) { spawnEggModel() }

        props {
            sized(1f, 1f)
        }

        attributes {
            maxHealth           =  5.0
            movementSpeed       =  0.25
            movementEfficiency  =  1.0
            followRange         = 15.0
            gravity             =  1.0
            scale               =  1.0
            stepHeight          =  1.0
            maxAbsorption       = 50.0
            armor               =  0.0
            armorToughness      =  0.0
            knockbackResistance =  0.0
        }
    }

    val myMobSpawnEgg by ::myMobType.spawnEgg
}