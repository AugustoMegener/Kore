package io.kito.kore.client.gui.screens.inventory

import io.kito.kore.common.world.inventory.KMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

abstract class KContainerScreen<T : KMenu>(menu: T, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<T>(menu, playerInventory, title)
{
    abstract val backgroundTexture: ResourceLocation


    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(backgroundTexture, guiLeft, guiTop, 0, 0, imageWidth, imageHeight)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}