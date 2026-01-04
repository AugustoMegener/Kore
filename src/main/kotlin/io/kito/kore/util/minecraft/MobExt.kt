package io.kito.kore.util.minecraft

import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.GoalSelector

/**
 * Utility object providing extension functions and classes for Minecraft [Mob]s.
 */
object MobExt {
    /**
     * Operator function to add a [GoalData] to a [GoalSelector].
     * @param goalData The [GoalData] containing the priority and the [Goal].
     */
    operator fun GoalSelector.plusAssign(goalData: GoalData) {
        addGoal(goalData.priority, goalData.goal)
    }

    /**
     * Creates a [GoalData] instance for a [Mob].
     * @param priority The priority of the goal.
     * @param goalSuppler A lambda that supplies a [Goal] given the [Mob] instance.
     * @return A [GoalData] instance.
     */
    fun Mob.goal(priority: Int, goalSuppler: (Mob) -> Goal) = GoalData(priority, goalSuppler(this))

    /**
     * Applies multiple [GoalData] instances to a [Mob]
     * @param goalData A vararg of [GoalData] instances to apply to the goal selector.
     */
    fun Mob.onGoalSelector(vararg goalData: GoalData) { goalData.forEach { goalSelector += it } }

    /**
     * Applies multiple [GoalData] instances to a [Mob]
     * @param goalData A vararg of [GoalData] instances to apply to the target selector.
     */
    fun Mob.onTargetSelector(vararg goalData: GoalData) { goalData.forEach { targetSelector += it } }

    /**
     * Data class representing a goal with its priority.
     * @property priority The priority of the goal.
     * @property goal The [Goal] instance.
     */
    data class GoalData(val priority: Int, val goal: Goal)


    /**
     * Extension property for [AttributeSupplier.Builder] to set the maximum health attribute.
     */
    var AttributeSupplier.Builder.maxHealth: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(MAX_HEALTH, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the movement speed attribute.
     */
    var AttributeSupplier.Builder.movementSpeed: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(MOVEMENT_SPEED, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the movement efficiency attribute.
     */
    var AttributeSupplier.Builder.movementEfficiency: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(MOVEMENT_EFFICIENCY, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the follow range attribute.
     */
    var AttributeSupplier.Builder.followRange: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(FOLLOW_RANGE, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the gravity attribute.
     */
    var AttributeSupplier.Builder.gravity: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(GRAVITY, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the scale attribute.
     */
    var AttributeSupplier.Builder.scale: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(SCALE, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the step height attribute.
     */
    var AttributeSupplier.Builder.stepHeight: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(STEP_HEIGHT, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the maximum absorption attribute.
     */
    var AttributeSupplier.Builder.maxAbsorption: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(MAX_ABSORPTION, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the armor attribute.
     */
    var AttributeSupplier.Builder.armor: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(ARMOR, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the armor toughness attribute.
     */
    var AttributeSupplier.Builder.armorToughness: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(ARMOR_TOUGHNESS, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the knockback resistance attribute.
     */
    var AttributeSupplier.Builder.knockbackResistance: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(KNOCKBACK_RESISTANCE, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the explosion knockback resistance attribute.
     */
    var AttributeSupplier.Builder.explosionKnockbackResistance: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(EXPLOSION_KNOCKBACK_RESISTANCE, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the attack damage attribute.
     */
    var AttributeSupplier.Builder.attackDamage: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(ATTACK_DAMAGE, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the attack knockback attribute.
     */
    var AttributeSupplier.Builder.attackKnockback: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(ATTACK_KNOCKBACK, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the attack speed attribute.
     */
    var AttributeSupplier.Builder.attackSpeed: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(ATTACK_SPEED, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the flying speed attribute.
     */
    var AttributeSupplier.Builder.flyingSpeed: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(FLYING_SPEED, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the jump strength attribute.
     */
    var AttributeSupplier.Builder.jumpStrength: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(JUMP_STRENGTH, value) }

    /**
     * Extension property for [AttributeSupplier.Builder] to set the luck attribute.
     */
    var AttributeSupplier.Builder.luck: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(LUCK, value) }

    var AttributeSupplier.Builder.waypointTransmitRange: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(WAYPOINT_TRANSMIT_RANGE, value) }

    var AttributeSupplier.Builder.waypointReceiveRange: Double
        get() = throw IllegalStateException("Can\'t get a set-only var")
        set(value) { add(WAYPOINT_RECEIVE_RANGE, value) }

    var AttributeSupplier.Builder.blockBreakSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(BLOCK_BREAK_SPEED, value) }

    var AttributeSupplier.Builder.blockInteractionRange: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(BLOCK_INTERACTION_RANGE, value) }

    var AttributeSupplier.Builder.burningTime: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(BURNING_TIME, value) }

    var AttributeSupplier.Builder.cameraDistance: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(CAMERA_DISTANCE, value) }

    var AttributeSupplier.Builder.entityInteractionRange: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ENTITY_INTERACTION_RANGE, value) }

    var AttributeSupplier.Builder.fallDamageMultiplier: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(FALL_DAMAGE_MULTIPLIER, value) }

    var AttributeSupplier.Builder.miningEfficiency: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(MINING_EFFICIENCY, value) }

    var AttributeSupplier.Builder.oxygenBonus: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(OXYGEN_BONUS, value) }

    var AttributeSupplier.Builder.safeFallDistance: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SAFE_FALL_DISTANCE, value) }

    var AttributeSupplier.Builder.sneakingSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SNEAKING_SPEED, value) }

    var AttributeSupplier.Builder.spawnReinforcementsChance: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SPAWN_REINFORCEMENTS_CHANCE, value) }

    var AttributeSupplier.Builder.submergedMiningSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SUBMERGED_MINING_SPEED, value) }

    var AttributeSupplier.Builder.sweepingDamageRatio: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SWEEPING_DAMAGE_RATIO, value) }

    var AttributeSupplier.Builder.temptRange: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(TEMPT_RANGE, value) }

    var AttributeSupplier.Builder.waterMovementEfficiency: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(WATER_MOVEMENT_EFFICIENCY, value) }


}

