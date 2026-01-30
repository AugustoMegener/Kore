package io.kito.kore_tests.client.gui

import io.kito.kore.Kore.ID
import io.kito.kore.client.gui.RegisterLayer
import io.kito.kore.client.gui.RegisterLayer.LayerRegisterMode.ABOVE
import io.kito.kore.client.gui.kanvas.guiRoot
import io.kito.kore.client.gui.kanvas.renderTree
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.resource.KvsReloadListener.getKanvas
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@Scan
object GuiLayers {

    @RegisterLayer("$ID:my_hud", ABOVE)
    fun GuiGraphics.myHud(delta: DeltaTracker) {
        //blit(RenderPipelines.GUI, local("block_top").block.texture.png, 16, 16, 50, 50)

        getKanvas(loc("kore", "test"))?.let { guiRoot(this, it).renderTree(this, delta.gameTimeDeltaTicks) }
    }
}