package io.kito.kore_tests.common.world.inventory

import io.kito.kore.common.world.inventory.KMenuBlockEntity
import io.kito.kore.common.world.inventory.RegisterMenu
import io.kito.kore.common.world.inventory.RegisterMenu.Companion.menuType
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.IndexModifier
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot

@RegisterMenu("custom_menu")
class CustomMenu(containerId: Int, playerInv: Inventory, blockEntity: CustomBlockEntity) :
    KMenuBlockEntity<CustomBlockEntity>(blockEntity, playerInv, menuType(CustomMenu::class), containerId)
{
    val input1Slot by MenuSlot(ResourceHandlerSlot(blockEntity.inventory, { _, _, _ ->}, 0, 52, 15))

    val input2Slot by MenuSlot(ResourceHandlerSlot(blockEntity.inventory, { _, _, _ ->}, 1, 52, 52))

    val outputSlot by MenuSlot(
        object : ResourceHandlerSlot(blockEntity.inventory, { _, _, _ ->}, 2, 110, 34) {
            override fun mayPlace(stack: ItemStack) = false
        }
    )

    constructor(containerId: Int, inv: Inventory, buf: RegistryFriendlyByteBuf) :
            this(containerId, inv, getBE(inv, buf))
}