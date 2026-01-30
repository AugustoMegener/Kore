package io.kito.kore.client.gui.kanvas.ctx

import io.kito.kore.client.gui.screens.inventory.IKvsScreen
import io.kito.kore.common.world.inventory.IKvsMenu
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu


open class ContainerCtx<T, M>(val screen: T?, val menu: M) where M : AbstractContainerMenu,
                                                                 M : IKvsMenu,
                                                                 T : AbstractContainerScreen<M>,
                                                                 T : IKvsScreen
{
    companion object {

    }
}