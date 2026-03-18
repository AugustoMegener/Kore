package io.kito.kore.client.gui.kanvas.ctx

import io.kito.kore.client.gui.kanvas.theme.Theme.slotBack
import io.kito.kore.client.gui.kanvas.node.Box.Companion.box
import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.node.Texture.Companion.texture
import io.kito.kore.client.gui.kanvas.resolveAbsolutePosition
import io.kito.kore.client.gui.kanvas.theme.KvsTexture
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Centered
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.px
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.slotSize
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Sum.Companion.plus
import io.kito.kore.client.gui.screens.inventory.IKvsScreen
import io.kito.kore.common.world.inventory.IKvsMenu
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import org.joml.Vector2i
import org.joml.component1
import org.joml.component2


abstract class ContainerCtx<T, M>(val menu: M, val screen: T? = null)
        where M : AbstractContainerMenu,
              M : IKvsMenu,
              T : AbstractContainerScreen<M>,
              T : IKvsScreen
{
    private val nodeSlots = hashMapOf<Pair<String, Int>, KvsNode>()

    val slotsPos get() = nodeSlots.map { (i, it) -> i to it.resolveAbsolutePosition() }.toMap()

    protected fun slot(containerId: String, idx: Int, node: KvsNode) {
        nodeSlots[containerId to idx] = node.also { it.scale = slotSize }
    }

    protected fun KvsNode.bareSlot(containerId: String, idx: Int, pos: KvsVec) {
        slot(containerId, idx, box(pos, slotSize))
    }

    protected fun KvsNode.slot(containerId: String, idx: Int, pos: KvsVec) {
        slot(containerId, idx, slotBack(pos, px(18)).box(Centered, slotSize))
    }

    protected fun KvsNode.slot(containerId: String, idx: Int, texture: KvsTexture, pos: KvsVec, size: KvsVec = slotSize) {
        slot(containerId, idx, texture(texture, pos, size).box(Centered, slotSize))
    }


    protected fun KvsNode.slotLine(containerId: String, pos: KvsVec, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it, slotBack(pos + px(18 * it, 0), px(18)).box(Centered, slotSize))
        }
    }

    protected fun KvsNode.bareSlotLine(containerId: String, pos: KvsVec, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it, box(pos + px(18 * it, 0), slotSize))
        }
    }

    protected fun KvsNode.slotLine(containerId: String, pos: KvsVec, texture: KvsTexture, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it,
                texture(texture, pos + px(18 * it, 0), px(18)).box(Centered, slotSize)
            )
        }
    }

    protected fun KvsNode.slotLine(containerId: String, pos: KvsVec, sizeSlot: KvsVec.Absolute, texture: KvsTexture,
                                   firstIdx: Int, slotAmount: Int)
    {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it,
                texture(texture, pos + px(sizeSlot.x * (it + 1), 0), sizeSlot).box(Centered, slotSize)
            )
        }
    }


    protected fun KvsNode.slotColumn(containerId: String, pos: KvsVec, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it,
                slotBack(pos + px(0, 18 * it), px(18)).box(Centered, slotSize)
            )
        }
    }

    protected fun KvsNode.bareSlotColumn(containerId: String, pos: KvsVec, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it, box(pos + px(0, 18 * it), slotSize))
        }
    }

    protected fun KvsNode.slotColumn(containerId: String, pos: KvsVec, texture: KvsTexture, firstIdx: Int, slotAmount: Int) {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it,
                texture(texture, pos + px(0, 18 * it), px(18)).box(Centered, slotSize)
            )
        }
    }

    protected fun KvsNode.slotColumn(containerId: String, pos: KvsVec, sizeSlot: KvsVec.Absolute, texture: KvsTexture,
                                     firstIdx: Int, slotAmount: Int)
    {
        repeat(slotAmount) {
            slot(containerId, firstIdx + it,
                texture(texture, pos + px(0, sizeSlot.y * (it + 1)), sizeSlot).box(Centered, slotSize)
            )
        }
    }


    protected fun KvsNode.slotGrid(containerId: String, pos: KvsVec, firstIdx: Int, lineSlotAmount: Int, linesAmount: Int) {
        repeat(linesAmount) {
            slotLine(containerId, pos, firstIdx + lineSlotAmount * it, lineSlotAmount)
        }
    }

    protected fun KvsNode.bareSlotGrid(containerId: String, pos: KvsVec, firstIdx: Int, lineSlotAmount: Int, linesAmount: Int) {
        repeat(linesAmount) {
            bareSlotLine(containerId, pos, firstIdx + lineSlotAmount * it, lineSlotAmount)
        }
    }

    protected fun KvsNode.slotGrid(containerId: String, pos: KvsVec, texture: KvsTexture, firstIdx: Int,
                                   lineSlotAmount: Int, linesAmount: Int)
    {
        repeat(linesAmount) {
            slotLine(containerId, pos, texture, firstIdx + lineSlotAmount * it, lineSlotAmount)
        }
    }

    protected fun KvsNode.slotGrid(containerId: String, pos: KvsVec, sizeSlot: KvsVec.Absolute, texture: KvsTexture,
                                   firstIdx: Int, lineSlotAmount: Int, linesAmount: Int)
    {
        repeat(linesAmount) {
            slotLine(containerId, pos, sizeSlot, texture, firstIdx + lineSlotAmount * it, lineSlotAmount)
        }
    }


    protected fun KvsNode.slotColumnGrid(containerId: String, pos: KvsVec, firstIdx: Int, columnSlotAmount: Int, columnsAmount: Int) {
        repeat(columnsAmount) {
            slotColumn(containerId, pos, firstIdx + columnSlotAmount * it, columnSlotAmount)
        }
    }

    protected fun KvsNode.bareSlotColumnGrid(containerId: String, pos: KvsVec, firstIdx: Int, columnSlotAmount: Int, columnsAmount: Int) {
        repeat(columnsAmount) {
            bareSlotColumn(containerId, pos, firstIdx + columnSlotAmount * it, columnSlotAmount)
        }
    }

    protected fun KvsNode.slotColumnGrid(containerId: String, pos: KvsVec, texture: KvsTexture, firstIdx: Int,
                                         columnSlotAmount: Int, columnsAmount: Int)
    {
        repeat(columnsAmount) {
            slotColumn(containerId, pos, texture, firstIdx + columnSlotAmount * it, columnSlotAmount)
        }
    }

    protected fun KvsNode.slotColumnGrid(containerId: String, pos: KvsVec, sizeSlot: KvsVec.Absolute, texture: KvsTexture,
                                         firstIdx: Int, columnSlotAmount: Int, columnsAmount: Int)
    {
        repeat(columnsAmount) {
            slotColumn(containerId, pos, sizeSlot, texture, firstIdx + columnSlotAmount * it, columnSlotAmount)
        }
    }

    fun KvsNode.playerHotbar(pos: KvsVec) = slotLine("playerInventory", pos, 0, 9)
    fun KvsNode.playerColumnHotbar(pos: KvsVec) = slotLine("playerInventory", pos, 0, 9)

    fun KvsNode.playerInventory(pos: KvsVec, lineSlotAmount: Int, linesAmount: Int) =
        slotGrid("playerInventory", pos, 9, lineSlotAmount, linesAmount)

    fun KvsNode.playerInventory(pos: KvsVec) = slotGrid("playerInventory", pos, 9, 9, 3)

    fun KvsNode.playerColumnInventory(pos: KvsVec, lineSlotAmount: Int, linesAmount: Int) =
        slotColumnGrid("playerInventory", pos, 9, lineSlotAmount, linesAmount)

    fun KvsNode.playerColumnInventory(pos: KvsVec) = slotColumnGrid("playerInventory", pos, 9, 9, 3)

    companion object {
        fun IKvsMenu.initSlots(slotsPos: Map<Pair<String, Int>, Vector2i>) {
            slotsPos.forEach { (id, idx), (x, y) -> initSlot(id, idx, x, y) }
        }
    }
}