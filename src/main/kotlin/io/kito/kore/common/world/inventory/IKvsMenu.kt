package io.kito.kore.common.world.inventory

import net.minecraft.world.inventory.Slot

interface IKvsMenu {

    val slotStack: Collection<Slot>

    fun add(slot: Slot)
}