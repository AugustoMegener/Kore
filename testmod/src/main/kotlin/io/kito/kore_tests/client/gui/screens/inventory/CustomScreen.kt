package io.kito.kore_tests.client.gui.screens.inventory

import io.kito.kore.client.gui.screens.inventory.ContainerScreenRegistry
import io.kito.kore.client.gui.screens.inventory.KContainerScreen
import io.kito.kore.client.gui.screens.inventory.RegisterContainerScreen
import io.kito.kore.common.world.inventory.RegisterMenu.Companion.menuType
import io.kito.kore.util.minecraft.ResourceLocationExt.container
import io.kito.kore.util.minecraft.ResourceLocationExt.gui
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.world.inventory.CustomMenu
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class CustomScreen(menu: CustomMenu, playerInventory: Inventory, title: Component) :
    KContainerScreen<CustomMenu>(menu, playerInventory, title)
{
    override val backgroundTexture = local("custom_screen").container.gui.texture.png

    @RegisterContainerScreen
    companion object : ContainerScreenRegistry<CustomMenu>({ menuType(CustomMenu::class) }, ::CustomScreen)
}