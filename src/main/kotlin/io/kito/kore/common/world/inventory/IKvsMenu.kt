package io.kito.kore.common.world.inventory

import net.minecraft.world.inventory.Slot

typealias SlotBuilder = (x: Int, y: Int) -> Slot

interface IKvsMenu {

    fun initSlot(id: String, idx: Int, x: Int, y: Int): Slot
}