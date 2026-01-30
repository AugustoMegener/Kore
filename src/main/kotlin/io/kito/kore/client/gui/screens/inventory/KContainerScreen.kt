package io.kito.kore.client.gui.screens.inventory

import io.kito.kore.common.world.inventory.KMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory


abstract class KContainerScreen<T : KMenu>(menu: T, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<T>(menu, playerInventory, title)
{
    abstract val backgroundTexture: ResourceLocation
    abstract val backgroundWidth: Int
    abstract val backgroundHeight: Int

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED, backgroundTexture, guiLeft, guiTop, 0f, 0f,
            imageWidth, imageHeight, backgroundWidth, backgroundHeight
        )
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)


        renderSlots()
    }
}

