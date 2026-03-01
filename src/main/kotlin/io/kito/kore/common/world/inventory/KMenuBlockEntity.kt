package io.kito.kore.common.world.inventory

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.entity.BlockEntity

abstract class KMenuBlockEntity<T: BlockEntity>(val blockEntity: T,
                                                playerInv: Inventory,
                                                menuType: MenuType<*>,
                                                containerId: Int) : KMenu(menuType, containerId, playerInv)
{
    override val level = blockEntity.level
    override val levelAccess: ContainerLevelAccess by lazy { ContainerLevelAccess.create(level!!, blockEntity.blockPos) }

    override fun stillValid(player: Player) = stillValid(levelAccess, player, blockEntity.blockState.block)
}