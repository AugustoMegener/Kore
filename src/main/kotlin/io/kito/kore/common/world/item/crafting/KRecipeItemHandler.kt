package io.kito.kore.common.world.item.crafting

import net.minecraft.core.HolderLookup
import net.neoforged.neoforge.common.crafting.ICustomIngredient
import net.neoforged.neoforge.common.crafting.SizedIngredient
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

abstract class KRecipeItemHandler<T : ItemHandlerRecipeInput<*>> : KRecipe<T>() {
    override fun assembleInput(slot: Int,
                               ingredient: ICustomIngredient,
                               input: T,
                               registries: HolderLookup.Provider)
        { if (slot !in keepSlots)  input.extractItem(slot, 1, false) }

    override fun assembleInput(slot: Int,
                               ingredient: SizedIngredient,
                               input: T,
                               registries: HolderLookup.Provider)
        { if (slot !in keepSlots) input.extractItem(slot, ingredient.count(), false) }

    companion object {
        val KRecipe<*>.keepSlots get() =
            this::class.memberProperties
                .filter { it.hasAnnotation<Keep>() }
                .mapNotNull { it.findAnnotation<Slot>()?.slot }
    }
}