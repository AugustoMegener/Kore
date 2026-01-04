package io.kito.kore.util.neoforge

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.resource.Resource


object ItemHandlerExt {

    operator fun <T : Resource> ResourceHandler<T>.get(idx: Int): T = getResource(idx)
    
    fun stackHandlerOf(vararg stack: ItemStack) = ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, *stack))

    
    fun stackHandlerOf(size: Int, vararg stacks: Pair<Int, ItemStack>) =
        ItemStacksResourceHandler(NonNullList.withSize(size, ItemStack.EMPTY))
            .also { stacks.forEach { (i, s) -> it.set(i, ItemResource.of(s), s.count) } }
}

