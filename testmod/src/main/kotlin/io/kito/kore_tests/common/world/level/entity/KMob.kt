package io.kito.kore_tests.common.world.level.entity

import io.kito.kore.util.minecraft.MobExt.goal
import io.kito.kore.util.minecraft.MobExt.onGoalSelector
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootTable

class KMob(entityType: EntityType<out Mob>, level: Level) : Mob(entityType, level) {
    override fun registerGoals() {
        onGoalSelector(goal(0, ::FloatGoal))
    }
}