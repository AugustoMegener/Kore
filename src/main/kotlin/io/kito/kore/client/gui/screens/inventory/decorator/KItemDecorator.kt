package io.kito.kore.client.gui.screens.inventory.decorator

import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.client.IItemDecorator

/**
 * Interface for custom item decorators in Kore.
 * This extends NeoForge's [IItemDecorator] to allow for specific items to be targeted by the decorator.
 * Item decorators can be used to add custom rendering or visual effects to items within GUI screens.
 */
interface KItemDecorator : IItemDecorator {

    /**
     * A list of [ItemLike] objects that this decorator should apply to.
     * Only items present in this list will be affected by the decorator's rendering logic.
     */
    val targetItems: List<ItemLike>
}

