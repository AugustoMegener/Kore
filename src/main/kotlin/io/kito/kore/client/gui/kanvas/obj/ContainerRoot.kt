package io.kito.kore.client.gui.kanvas.obj

import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Function.Companion.fn
import net.minecraft.client.gui.screens.inventory.ContainerScreen

class ContainerRoot(screen: ContainerScreen, imageWidth: Int, imageHeight: Int) : Root({ imageWidth }, { imageHeight })
{
    override var pos: KvsVec = fn({ screen.guiTop }, { screen.guiLeft })
}