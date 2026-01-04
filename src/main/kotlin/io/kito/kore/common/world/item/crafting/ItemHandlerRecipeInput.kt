package io.kito.kore.common.world.item.crafting

import io.kito.kore.util.neoforge.ItemHandlerExt.get
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.TransactionContext

abstract class ItemHandlerRecipeInput<T: ResourceHandler<ItemResource>>(val handler: T) :
    ResourceHandler<ItemResource>, RecipeInput
{

    abstract val indexes: Map<Int, Int>

    override fun getItem(index: Int): ItemStack = handler[index].toStack(handler.getAmountAsInt(index))

    override fun size() = indexes.keys.max()


    override fun getResource(slot: Int): ItemResource =
        handler[indexes[slot] ?: throw IllegalStateException("Invalid index: $slot")]


    override fun insert(index: Int, resource: ItemResource, amount: Int, transaction: TransactionContext) =
        handler.insert(indexes[index] ?: throw IllegalStateException("Invalid index: $index"), resource, amount, transaction)


    override fun extract(slot: Int, resource: ItemResource, amount: Int, transaction: TransactionContext): Int =
        handler.extract(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), resource, amount, transaction)

    override fun getCapacityAsInt(slot: Int, resource: ItemResource): Int =
        handler.getCapacityAsInt(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), resource)

    override fun isValid(slot: Int, resource: ItemResource): Boolean =
        handler.isValid(indexes[slot] ?: throw IllegalStateException("Invalid index: $slot"), resource)

    override fun getAmountAsLong(index: Int) = getAmountAsInt(index).toLong()

    override fun getCapacityAsLong(index: Int, resource: ItemResource) = getCapacityAsInt(index, resource).toLong()
}