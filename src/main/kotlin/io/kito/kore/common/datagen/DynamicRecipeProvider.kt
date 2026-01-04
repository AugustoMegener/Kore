package io.kito.kore.common.datagen

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import java.util.*
import java.util.concurrent.CompletableFuture

class DynamicRecipeProvider(output: PackOutput,
                            registries: CompletableFuture<HolderLookup.Provider>,
                            private val entries : List<(RecipeOutput, HolderLookup.Provider, RecipeProvider) -> Unit>) :
    RecipeProvider.Runner(output, registries)
{


    override fun createRecipeProvider(registries: HolderLookup.Provider, output: RecipeOutput) =
        object : RecipeProvider(registries, output) {
            override fun buildRecipes() {
                entries.forEach { it(output, registries, this) }

            }
        }


    override fun getName() = ""

    companion object {

        fun HolderLookup.Provider.has(count: MinMaxBounds.Ints, item: ItemLike) =
            inventoryTrigger(ItemPredicate.Builder.item().of(lookupOrThrow(Registries.ITEM), item).withCount(count))

        fun HolderLookup.Provider.has(itemLike: ItemLike) =
            inventoryTrigger(ItemPredicate.Builder.item().of(lookupOrThrow(Registries.ITEM), itemLike))


        fun HolderLookup.Provider.has(tag: TagKey<Item>)=
             inventoryTrigger(ItemPredicate.Builder.item().of(lookupOrThrow(Registries.ITEM), tag))


        fun inventoryTrigger(vararg items: ItemPredicate.Builder) =
            inventoryTrigger(*items.map(ItemPredicate.Builder::build).toTypedArray())


        fun inventoryTrigger(vararg predicates: ItemPredicate): Criterion<InventoryChangeTrigger.TriggerInstance> =
            CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(
                    InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        listOf(*predicates)
                    )
                )
    }
}


