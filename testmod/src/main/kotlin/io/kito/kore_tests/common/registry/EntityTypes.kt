package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.EntityTypeRegister
import io.kito.kore.util.minecraft.LootTableExt.pool
import io.kito.kore.util.minecraft.MobExt.armor
import io.kito.kore.util.minecraft.MobExt.armorToughness
import io.kito.kore.util.minecraft.MobExt.explosionKnockbackResistance
import io.kito.kore.util.minecraft.MobExt.followRange
import io.kito.kore.util.minecraft.MobExt.gravity
import io.kito.kore.util.minecraft.MobExt.knockbackResistance
import io.kito.kore.util.minecraft.MobExt.maxAbsorption
import io.kito.kore.util.minecraft.MobExt.maxHealth
import io.kito.kore.util.minecraft.MobExt.movementEfficiency
import io.kito.kore.util.minecraft.MobExt.movementSpeed
import io.kito.kore.util.minecraft.MobExt.scale
import io.kito.kore.util.minecraft.MobExt.stepHeight
import io.kito.kore_tests.DataGenerator.entityLootTable
import io.kito.kore_tests.DataGenerator.spawnEggModel
import io.kito.kore_tests.ID
import io.kito.kore_tests.client.renderer.KMobRenderer
import io.kito.kore_tests.common.world.level.entity.KMob
import net.minecraft.data.loot.EntityLootSubProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory.MISC
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootPool.lootPool
import net.minecraft.world.level.storage.loot.LootTable.lootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.stream.Stream

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
            explosionKnockbackResistance = 0.0
        }

        entityLootTable { lookup, eType ->
            object : EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags(), lookup) {
                override fun generate() {
                    add(eType, lootTable().pool {
                        setRolls(exactly(1.0f))
                        add(lootTableItem(myMobSpawnEgg).apply(setCount(ConstantValue(1f))))
                    })
                }

                override fun getKnownEntityTypes(): Stream<EntityType<*>> = Stream.of(eType)
            }
        }
    }

    /**
     * Lazily initialized property to get the spawn egg associated with `myMobType`.
     */
    val myMobSpawnEgg: SpawnEggItem by ::myMobType.spawnEgg
}

