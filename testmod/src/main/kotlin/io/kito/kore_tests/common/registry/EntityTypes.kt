package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.EntityTypeRegister
import io.kito.kore.util.minecraft.MobExt.armor
import io.kito.kore.util.minecraft.MobExt.armorToughness
import io.kito.kore.util.minecraft.MobExt.attackDamage
import io.kito.kore.util.minecraft.MobExt.attackKnockback
import io.kito.kore.util.minecraft.MobExt.attackSpeed
import io.kito.kore.util.minecraft.MobExt.blockBreakSpeed
import io.kito.kore.util.minecraft.MobExt.blockInteractionRange
import io.kito.kore.util.minecraft.MobExt.burningTime
import io.kito.kore.util.minecraft.MobExt.cameraDistance
import io.kito.kore.util.minecraft.MobExt.entityInteractionRange
import io.kito.kore.util.minecraft.MobExt.explosionKnockbackResistance
import io.kito.kore.util.minecraft.MobExt.fallDamageMultiplier
import io.kito.kore.util.minecraft.MobExt.flyingSpeed
import io.kito.kore.util.minecraft.MobExt.followRange
import io.kito.kore.util.minecraft.MobExt.gravity
import io.kito.kore.util.minecraft.MobExt.jumpStrength
import io.kito.kore.util.minecraft.MobExt.knockbackResistance
import io.kito.kore.util.minecraft.MobExt.luck
import io.kito.kore.util.minecraft.MobExt.maxAbsorption
import io.kito.kore.util.minecraft.MobExt.maxHealth
import io.kito.kore.util.minecraft.MobExt.miningEfficiency
import io.kito.kore.util.minecraft.MobExt.movementEfficiency
import io.kito.kore.util.minecraft.MobExt.movementSpeed
import io.kito.kore.util.minecraft.MobExt.oxygenBonus
import io.kito.kore.util.minecraft.MobExt.safeFallDistance
import io.kito.kore.util.minecraft.MobExt.scale
import io.kito.kore.util.minecraft.MobExt.sneakingSpeed
import io.kito.kore.util.minecraft.MobExt.spawnReinforcementsChance
import io.kito.kore.util.minecraft.MobExt.stepHeight
import io.kito.kore.util.minecraft.MobExt.submergedMiningSpeed
import io.kito.kore.util.minecraft.MobExt.sweepingDamageRatio
import io.kito.kore.util.minecraft.MobExt.temptRange
import io.kito.kore.util.minecraft.MobExt.waterMovementEfficiency
import io.kito.kore.util.minecraft.MobExt.waypointReceiveRange
import io.kito.kore.util.minecraft.MobExt.waypointTransmitRange
import io.kito.kore_tests.DataGenerator.defaultModel
import io.kito.kore_tests.ID
import io.kito.kore_tests.client.renderer.KMobRenderer
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.world.entity.MobCategory.MISC
import net.minecraft.world.item.SpawnEggItem
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

/**
 * Registers custom entity types for the Kore Tests mod.
 * Annotated with `@Scan` to be automatically discovered by Kore for entity type registration.
 * Extends `EntityTypeRegister` with the mod ID, providing a DSL for defining entity types.
 */
@Scan
object EntityTypes : EntityTypeRegister(ID) {

    /**
     * Defines a custom mob entity type named "mob".
     * - `ofMob (::KMob to MISC)`: Specifies the entity class `KMob` and its `MobCategory` as `MISC`.
     * - `renderer`: Associates `KMobRenderer` for rendering this entity.
     * - `spawnEgg`: Configures the spawn egg with colors and model generation.
     * - `props`: Sets entity properties like `sized`.
     * - `attributes`: Defines various attributes for the mob, such as `maxHealth`, `movementSpeed`, `armor`, etc.
     */
    val myMobType by "mob" ofMob (::KMob to MISC) that {
        renderer(::KMobRenderer)

        spawnEgg { defaultModel() }

        props {
            sized(1f, 1f)
        }

        attributes {
            maxHealth                       =  5.0
            movementSpeed                    = 0.25
            movementEfficiency               = 1.0
            followRange                      = 15.0
            gravity                          = 1.0
            scale                            = 1.0
            stepHeight                       = 1.0
            maxAbsorption                   = 50.0
            armor                           = 0.0
            armorToughness                    = 0.0
            knockbackResistance              = 0.0
            explosionKnockbackResistance    = 0.0
            attackDamage                      = 0.0
            attackKnockback                   = 0.0
            attackSpeed                       = 0.0
            flyingSpeed                        = 0.0
            jumpStrength                       = 0.0
            luck                               = 0.0
            waypointTransmitRange              = 0.0
            waypointReceiveRange               = 0.0
            blockBreakSpeed                     = 0.0
            blockInteractionRange       = 0.0
            burningTime                         = 0.0
            cameraDistance                       = 0.0
            entityInteractionRange               = 0.0
            fallDamageMultiplier                 = 0.0
            miningEfficiency                     = 0.0
            oxygenBonus                          = 0.0
            safeFallDistance                      = 0.0
            sneakingSpeed                          = 0.0
            spawnReinforcementsChance              = 0.0
            submergedMiningSpeed                    = 0.0
            sweepingDamageRatio                     = 0.0
            temptRange                              = 0.0
            waterMovementEfficiency                 = 0.0
        }

    }

    /**
     * Lazily initialized property to get the spawn egg associated with `myMobType`.
     */
    val myMobSpawnEgg: SpawnEggItem by ::myMobType.spawnEgg
}

