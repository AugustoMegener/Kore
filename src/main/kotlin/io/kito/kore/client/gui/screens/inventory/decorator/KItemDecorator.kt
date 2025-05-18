package io.kito.kore.client.gui.screens.inventory.decorator

import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.client.IItemDecorator

interface KItemDecorator : IItemDecorator {

    val targetItems: List<ItemLike>
}