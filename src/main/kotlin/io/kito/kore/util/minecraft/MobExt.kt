package io.kito.kore.util.minecraft

import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.GoalSelector

object MobExt {
    operator fun GoalSelector.plusAssign(goalData: GoalData) {
        addGoal(goalData.priority, goalData.goal)
    }

    fun Mob.goal(priority: Int, goalSuppler: (Mob) -> Goal) = GoalData(priority, goalSuppler(this))

    fun Mob.onGoalSelector(vararg goalData: GoalData) { goalData.forEach { goalSelector += it } }

    fun Mob.onTargetSelector(vararg goalData: GoalData) { goalData.forEach { targetSelector += it } }

    data class GoalData(val priority: Int, val goal: Goal)


    var AttributeSupplier.Builder.maxHealth: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(MAX_HEALTH, value) }

    var AttributeSupplier.Builder.movementSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(MOVEMENT_SPEED, value) }

    var AttributeSupplier.Builder.movementEfficiency: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(MOVEMENT_EFFICIENCY, value) }

    var AttributeSupplier.Builder.followRange: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(FOLLOW_RANGE, value) }

    var AttributeSupplier.Builder.gravity: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(GRAVITY, value) }

    var AttributeSupplier.Builder.scale: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(SCALE, value) }

    var AttributeSupplier.Builder.stepHeight: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(STEP_HEIGHT, value) }

    var AttributeSupplier.Builder.maxAbsorption: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(MAX_ABSORPTION, value) }

    var AttributeSupplier.Builder.armor: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ARMOR, value) }

    var AttributeSupplier.Builder.armorToughness: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ARMOR_TOUGHNESS, value) }

    var AttributeSupplier.Builder.knockbackResistance: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(KNOCKBACK_RESISTANCE, value) }

    var AttributeSupplier.Builder.explosionKnockbackResistance: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(EXPLOSION_KNOCKBACK_RESISTANCE, value) }

    var AttributeSupplier.Builder.attackDamage: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ATTACK_DAMAGE, value) }

    var AttributeSupplier.Builder.attackKnockback: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ATTACK_KNOCKBACK, value) }

    var AttributeSupplier.Builder.attackSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(ATTACK_SPEED, value) }

    var AttributeSupplier.Builder.flyingSpeed: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(FLYING_SPEED, value) }

    var AttributeSupplier.Builder.jumpStrength: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(JUMP_STRENGTH, value) }

    var AttributeSupplier.Builder.luck: Double
        get() = throw IllegalStateException("Can't get a set-only var")
        set(value) { add(LUCK, value) }
}