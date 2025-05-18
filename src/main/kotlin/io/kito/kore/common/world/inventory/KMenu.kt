package io.kito.kore.common.world.inventory

import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import kotlin.reflect.KProperty

abstract class KMenu(val playerInv: Inventory, menuType: MenuType<*>, containerId: Int) :
    AbstractContainerMenu(menuType, containerId)
{
    open val hotbarX = 8
    open val hotbarY = 142

    open val hotbarSlotX: (index: Int) -> Int = { it * 18 }
    open val hotbarSlotY: (index: Int) -> Int = { 0 }

    open val playerInvX = 8
    open val playerInvY = 84

    open val playerInvSlotX: (index: Int) -> Int = { (it % 9) * 18 }
    open val playerInvSlotY: (index: Int) -> Int = { (it / 9) * 18 }

    abstract val level: Level?
    abstract val levelAccess: ContainerLevelAccess

    init { createInventory(playerInv) }

    private fun createInventory(playerInv: Inventory) {
        createPlayerHotbar(playerInv)
        createPlayerInventory(playerInv)
        createBlockEntityInventory()
    }

    open fun createPlayerHotbar(playerInv: Inventory) {
        repeat(9) { addSlot(Slot(playerInv, it, hotbarX + hotbarSlotX(it), hotbarY + hotbarSlotY(it))) }
    }

    open fun createPlayerInventory(playerInv: Inventory) {
        repeat(27) {
            addSlot(Slot(playerInv, 9 + it, playerInvX + playerInvSlotX(it), playerInvY + playerInvSlotY(it)))
        }
    }

    open fun createBlockEntityInventory() {}

    inner class MenuSlot(val slot: Slot) {
        init { addSlot(slot) }

        operator fun getValue(obj: KMenu, prop: KProperty<*>) = slot
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val fromSlot: Slot = getSlot(index)
        val fromStack: ItemStack = fromSlot.item

        if (fromStack.count <= 0) fromSlot.set(ItemStack.EMPTY)

        if (!fromSlot.hasItem()) return ItemStack.EMPTY

        val copyFromStack = fromStack.copy()

        val lastSlot = slots.size - 1

             if (index < 36) { if (!moveItemStackTo(fromStack, 36, lastSlot, false)) return ItemStack.EMPTY }
        else if (index < lastSlot) { if (!moveItemStackTo(fromStack, 0, 36, false)) return ItemStack.EMPTY }
        else                 { return ItemStack.EMPTY }

        fromSlot.setChanged()
        fromSlot.onTake(player, fromStack)

        fromSlot.set(ItemStack.EMPTY)

        return copyFromStack
    }

    companion object {
        @Suppress(UNCHECKED_CAST)
        fun <T : BlockEntity> getBE(inv: Inventory, buf: FriendlyByteBuf) =
            inv.player.level().getBlockEntity(buf.readBlockPos()) as T
    }
}