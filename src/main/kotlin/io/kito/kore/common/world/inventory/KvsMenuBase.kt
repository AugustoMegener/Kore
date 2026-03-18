
package io.kito.kore.common.world.inventory

import io.kito.kore.client.gui.kanvas.KanvasException
import io.kito.kore.client.gui.kanvas.ctx.ContainerCtx
import io.kito.kore.client.gui.kanvas.ctx.ContainerCtx.Companion.initSlots
import io.kito.kore.client.gui.screens.inventory.IKvsScreen
import io.kito.kore.util.minecraft.smartQuickMoveStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot

abstract class KvsMenuBase<T, M>(menuType: MenuType<*>,
                                 containerId: Int,
                                 val playerInv: Inventory,
                                 val ctx: ContainerCtx<T, M>) :
    AbstractContainerMenu(menuType, containerId), IKvsMenu
        where T : AbstractContainerScreen<M>,
              T : IKvsScreen,
              M : AbstractContainerMenu,
              M : IKvsMenu
{
    private val slotBuilders: HashMap<ContainerId, Map<Int, SlotBuilder>> = hashMapOf(
        playerInventory to (0..36).associateWith { { x, y -> Slot(playerInv, it, x, y) } }
    )

    //constructor(menuType: MenuType<*>, containerId: Int, playerInv: Inventory) : this()

    init {
        initSlots(ctx.slotsPos)
    }

    override fun quickMoveStack(player: Player, index: Int) = smartQuickMoveStack(player, index, ::moveItemStackTo)

    override fun initSlot(id: String, idx: Int, x: Int, y: Int): Slot = addSlot(
        slotBuilders[ContainerId(id)]?.get(idx)?.invoke(x, y) ?:
        throw KanvasException(NullPointerException("$id[$idx] slot not found!"))
    )

    inner class MenuSlot(val id: ContainerId, val idx: Int, slot: (idx: Int, x: Int, y: Int) -> Slot) {

        constructor(id: ContainerId, slot: (Int, Int, Int) -> Slot) : this(id, slotBuilders[id]?.size ?: 0, slot)

        init {
            if (id == playerInventory) throw IllegalArgumentException("${id.value} is reserved.")

            slotBuilders[id] = (slotBuilders.getOrElse(id) { mapOf() } + (idx to { x, y -> slot(idx, x, y) }))
        }
    }

    @JvmInline
    value class ContainerId(val value: String)

    companion object {
        val playerInventory = ContainerId("playerInventory")
    }
}