package io.kito.kore.util.minecraft

import io.kito.kore.util.minecraft.MobExt.goal
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.GoalSelector
import org.apache.logging.log4j.core.net.Priority

object MobExt {
    operator fun GoalSelector.plusAssign(goalData: GoalData) {
        addGoal(goalData.priority, goalData.goal)
    }

    fun Mob.goal(priority: Int, goalSuppler: (Mob) -> Goal) = GoalData(priority, goalSuppler(this))

    fun Mob.onGoalSelector(vararg goalData: GoalData) { goalData.forEach { goalSelector += it } }

    fun Mob.onTargetSelector(vararg goalData: GoalData) { goalData.forEach { targetSelector += it } }

    data class GoalData(val priority: Int, val goal: Goal)
}