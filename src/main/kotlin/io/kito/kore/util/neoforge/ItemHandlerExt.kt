package io.kito.kore.util.neoforge

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.ItemStackHandler

/**
 * Utility object providing extension functions for NeoForge item handlers.
 */
object ItemHandlerExt {
    /**
     * Operator function to set an [ItemStack] in a specific slot of an [IItemHandlerModifiable].
     * @param idx The index of the slot.
     * @param value The [ItemStack] to set.
     */
    operator fun IItemHandlerModifiable.set(idx: Int, value: ItemStack) { setStackInSlot(idx, value) }
    /**
     * Operator function to get the [ItemStack] from a specific slot of an [IItemHandler].
     * @param idx The index of the slot.
     * @return The [ItemStack] in the specified slot.
     */
    operator fun IItemHandler.get(idx: Int): ItemStack = getStackInSlot(idx)


    /**
     * Creates a new [ItemStackHandler] with initial stacks.
     * @param stack A vararg of [ItemStack]s to initialize the handler with.
     * @return A new [ItemStackHandler] instance.
     */
    fun stackHandlerOf(vararg stack: ItemStack) = ItemStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))
    /**
     * Creates a new [ItemStackHandler] with a specified size and initial stacks at specific indices.
     * @param size The number of slots in the item stack handler.
     * @param stacks A vararg of [Pair]s, where each pair consists of an index and an [ItemStack] to set at that index.
     * @return A new [ItemStackHandler] instance.
     */
    fun stackHandlerOf(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        ItemStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it[i] = s } }
}

