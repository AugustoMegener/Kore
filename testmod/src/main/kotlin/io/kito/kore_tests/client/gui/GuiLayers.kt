package io.kito.kore_tests.client.gui

import io.kito.kore.Kore.ID
import io.kito.kore.client.gui.RegisterLayer
import io.kito.kore.client.gui.RegisterLayer.LayerRegisterMode.ABOVE
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import io.kito.kore_tests.KoreTests.local
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines

@Scan
object GuiLayers {

    @RegisterLayer("$ID:my_hud", ABOVE)
    fun GuiGraphics.myHud(delta: DeltaTracker) {
        blit(RenderPipelines.GUI_TEXTURED, local("block_top").block.texture.png, 16, 16, 0f, 0f, 16, 16, 16, 16)

        /*getKanvas(loc("kore", "test"))?.let {
            guiRoot(this, it).renderTree(this, delta.gameTimeDeltaTicks)
        }*/


        /*blitSprite(
            RenderPipelines.GUI_TEXTURED,
            "themes/" on kanvasTheme + "/gui_background",
            50, 50,
            0, 0,
            0, 0,
            50, 50
        )*/
   }
}
