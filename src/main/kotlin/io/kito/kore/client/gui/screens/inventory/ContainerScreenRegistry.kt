package io.kito.kore.client.gui.screens.inventory

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

abstract class ContainerScreenRegistry<T : AbstractContainerMenu>(
    val menuType: () -> MenuType<T>, val supplier: (T, Inventory, Component) -> AbstractContainerScreen<T>
)