package io.kito.kore_tests.client.gui.screens.inventory.decorator

import io.kito.kore.client.gui.screens.inventory.decorator.KItemDecorator
import io.kito.kore.client.gui.screens.inventory.decorator.RegisterDecorator
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import io.kito.kore_tests.KoreTests.local
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries.ITEM
import net.minecraft.world.item.ItemStack

@RegisterDecorator
object MyDecorator : KItemDecorator {
    override val targetItems by lazy { ITEM.toList() }

    override fun render(guiGraphics: GuiGraphics, font: Font, stack: ItemStack, xOffset: Int, yOffset: Int): Boolean {
        guiGraphics.blit(local("block_top").block.texture.png, xOffset, yOffset, 0f, 0f, 2, 2, 2, 2)

        return false
    }
}