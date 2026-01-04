package io.kito.kore_tests.common.registry

import io.kito.kore.common.datagen.DynamicRecipeProvider.Companion.has
import io.kito.kore.common.event.RegisterTemplate
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.ItemRegister
import io.kito.kore.common.registry.RegistryTemplate
import io.kito.kore.common.template.Template.Companion.include
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.defaultModel
import io.kito.kore_tests.DataGenerator.flatModel
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.recipe
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import io.kito.kore_tests.common.registry.early.Strings.myGroup
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelTemplates.FLAT_ITEM
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.core.registries.BuiltInRegistries.ITEM
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items.STICK
import net.minecraft.world.item.crafting.Ingredient
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.*

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

            flatModel(local("item"))
        }
    }.include(myGroup)

    val exampleItem: Item by "example_item" of ::Item where {
        recipe { item, _ ->
            ShapedRecipeBuilder.shaped(lookupOrThrow(ITEM.key()), RecipeCategory.MISC, item.defaultInstance)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', Ingredient.of(STICK))
                .unlockedBy("stick", has { STICK })
        }

        flatModel(local("item"))
    }
}

