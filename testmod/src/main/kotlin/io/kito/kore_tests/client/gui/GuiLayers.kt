package io.kito.kore_tests.client.gui

import io.kito.kore.client.gui.RegisterLayer
import io.kito.kore.client.gui.RegisterLayer.LayerRegisterMode.ABOVE
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@Scan
object GuiLayers {

    @RegisterLayer("$ID:my_hud", ABOVE)
    fun GuiGraphics.myHud(delta: DeltaTracker) {
        blit(local("block_top").block.texture.png, 50, 50, 0f, 0f, 16, 16, 16, 16)
    }
}