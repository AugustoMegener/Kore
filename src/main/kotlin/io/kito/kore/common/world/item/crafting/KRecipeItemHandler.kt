package io.kito.kore.common.world.item.crafting


import io.kito.kore.Kore.logger
import io.kito.kore.util.neoforge.ItemHandlerExt.get
import net.minecraft.core.HolderLookup
import net.neoforged.neoforge.common.crafting.ICustomIngredient
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.transfer.transaction.Transaction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

abstract class KRecipeItemHandler<T : ItemHandlerRecipeInput<*>> : KRecipe<T>() {
    override fun assembleInput(slot: Int,
                               ingredient: ICustomIngredient,
                               input: T,
                               registries: HolderLookup.Provider)
    {
        if (slot !in keepSlots) {
            try {
                val tran = Transaction.open(null)
                input.extract(slot, input[slot],  1, tran)
            } catch (e: IllegalStateException) {
                logger.warn(e.message)
            }
        }
    }


    override fun assembleInput(slot: Int,
                               ingredient: SizedIngredient,
                               input: T,
                               registries: HolderLookup.Provider) {
        if (slot !in keepSlots)
            try {
                val tran = Transaction.open(null)
                input.extract(slot, input[slot], ingredient.count(), tran)
            } catch (e: IllegalStateException) {
                logger.warn(e.message)
            }
    }

    companion object {
        val KRecipe<*>.keepSlots get() =
            this::class.memberProperties
                .filter { it.hasAnnotation<Keep>() }
                .mapNotNull { it.findAnnotation<Slot>()?.slot }
    }
}