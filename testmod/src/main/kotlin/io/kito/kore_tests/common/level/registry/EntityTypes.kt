package io.kito.kore_tests.common.level.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.EntityTypeRegister
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.spawnEggModel
import io.kito.kore_tests.ID
import io.kito.kore_tests.client.renderer.KMobRenderer
import io.kito.kore_tests.common.level.entity.KMob
import net.minecraft.world.entity.MobCategory.MISC
import net.minecraft.world.entity.ai.attributes.Attributes.*
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
            add(MAX_HEALTH, 5.0)
            add(MOVEMENT_SPEED, 0.25)
            add(MOVEMENT_EFFICIENCY, 1.0)
            add(FOLLOW_RANGE, 15.0)
            add(GRAVITY, 1.0)
            add(SCALE, 1.0)
            add(STEP_HEIGHT, 1.0)
            add(MAX_ABSORPTION, 50.0)
            add(ARMOR, 0.0)
            add(ARMOR_TOUGHNESS, 0.0)
            add(KNOCKBACK_RESISTANCE, 0.0)
        }
    }

    val myMobSpawnEgg by ::myMobType.spawnEgg
}