package io.kito.kore.client.gui.screens.inventory

import io.kito.kore.common.world.inventory.KMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

/**
 * Abstract base class for custom container screens in Kore.
 * This class extends Minecraft's [AbstractContainerScreen] to provide common functionality
 * for rendering custom inventory and container GUIs, particularly by allowing a custom
 * background texture to be easily defined and rendered.
 *
 * @param T The type of [KMenu] that this screen is associated with. This ensures type safety
 *          between the screen and its corresponding menu.
 * @param menu The [KMenu] instance associated with this screen, providing access to the container's logic.
 * @param playerInventory The player's [Inventory], used for rendering the player's items.
 * @param title The [Component] representing the title of the screen, displayed at the top.
 */
abstract class KContainerScreen<T : KMenu>(menu: T, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<T>(menu, playerInventory, title)
{
    /**
     * The [ResourceLocation] pointing to the background texture for this container screen.
     * Subclasses must provide an implementation for this property to define their custom background.
     */
    abstract val backgroundTexture: ResourceLocation

    /**
     * Renders the background of the container screen.
     * This method is overridden to draw the custom `backgroundTexture` at the appropriate position.
     *
     * @param guiGraphics The [GuiGraphics] instance used for rendering GUI elements.
     * @param partialTick The partial tick time, used for smooth animations.
     * @param mouseX The current X-coordinate of the mouse cursor.
     * @param mouseY The current Y-coordinate of the mouse cursor.
     */
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(backgroundTexture, guiLeft, guiTop, 0, 0, imageWidth, imageHeight)
    }

    /**
     * Renders the entire container screen, including the background, foreground elements, and tooltips.
     * This method calls the superclass's render method and then renders the item tooltips.
     *
     * @param pGuiGraphics The [GuiGraphics] instance used for rendering GUI elements.
     * @param pMouseX The current X-coordinate of the mouse cursor.
     * @param pMouseY The current Y-coordinate of the mouse cursor.
     * @param pPartialTick The partial tick time.
     */
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}

