package io.kito.kore.util.minecraft

import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootPool.lootPool
import net.minecraft.world.level.storage.loot.LootTable

object LootTableExt {

    fun LootTable.Builder.pool(poolBuilder: LootPool.Builder.() -> Unit) =
        also { it.withPool(lootPool().apply(poolBuilder)) }
}