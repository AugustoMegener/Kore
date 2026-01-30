package io.kito.kore.common.world.inventory

import io.kito.kore.client.gui.kanvas.ctx.ContainerCtx
import io.kito.kore.util.minecraft.smartQuickMoveStack
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot

abstract class KvsMenuBase<T : ContainerCtx<*, *>>(menuType: MenuType<*>, containerId: Int) :
    AbstractContainerMenu(menuType, containerId), IKvsMenu
{
    override val slotStack = arrayListOf<Slot>()

    init {
        slotStack.forEach(::addSlot)
    }

    override fun quickMoveStack(player: Player, index: Int) = smartQuickMoveStack(player, index, ::moveItemStackTo)

    override fun add(slot: Slot) { slotStack += slot }

    abstract fun createCtx(): T
}