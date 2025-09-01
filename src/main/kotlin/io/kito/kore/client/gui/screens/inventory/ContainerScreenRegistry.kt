package io.kito.kore.client.gui.screens.inventory

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

/**
 * Abstract base class for registering container screens in Kore.
 * This class provides a structured way to associate a [MenuType] with its corresponding
 * [AbstractContainerScreen] factory, simplifying the process of creating custom GUI screens
 * for inventories and other container-based interactions.
 *
 * @param T The type of [AbstractContainerMenu] that this screen registry handles.
 * @property menuType A function that supplies the [MenuType] associated with this container screen.
 *                    This is typically a lazy-initialized property from a registry object.
 * @property supplier A lambda function that acts as a factory for creating instances of the
 *                    [AbstractContainerScreen]. It takes the menu, player inventory, and title component
 *                    as arguments, consistent with Minecraft's container screen constructors.
 */
abstract class ContainerScreenRegistry<T : AbstractContainerMenu, S>(
    val menuType: () -> MenuType<T>, val supplier: (T, Inventory, Component) -> S
) where S : Screen, S: MenuAccess<T>

