package io.kito.kore_tests.common.registry

import io.kito.kore.common.event.RegisterTemplate
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.ItemRegister
import io.kito.kore.common.registry.RegistryTemplate
import io.kito.kore.common.template.Template.Companion.include
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.shaped
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.recipe
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import io.kito.kore_tests.common.registry.early.Strings.myGroup
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items.STICK
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

/**
 * Registers custom item types for the Kore Tests mod.
 * Annotated with `@Scan` to be automatically discovered by Kore for item registration.
 * Extends `ItemRegister` with the mod ID, providing a DSL for defining item types.
 */
@Scan
object Items : ItemRegister(ID) {
    /**
     * A template for defining similar items programmatically.
     * It takes a string `i` to generate unique item names and localized names.
     * - `of ::Item`: Uses the generic `Item` class.
     * - `named`: Sets the localized names for the item (English and Brazilian Portuguese).
     * - `model`: Configures the item model to use a generated item model with a specific texture.
     */
    @RegisterTemplate
    val itemTemplate = RegistryTemplate(stringRegistry) { i: String ->
        "${i}_item" of ::Item where {
            named(EN_US to "${i.toTitle()} Item",
                  PT_BR to "Item ${i.toTitle()}")

            model { loc, _ ->
                getBuilder(loc.toString())
                    .parent(UncheckedModelFile("item/generated"))
                    .texture("layer0", local("item/item"))
            }
        }
    }.include(myGroup)

    val exampleItem: Item by "example_item" of ::Item where {
        recipe(local("gro")) {
            ShapedRecipe(ID, CraftingBookCategory.MISC,
                shaped("###",
                    "###",
                    "###")
                    .by('#' to Ingredient.of(STICK)),
                exampleItem.defaultInstance
            )
        }
    }
}

