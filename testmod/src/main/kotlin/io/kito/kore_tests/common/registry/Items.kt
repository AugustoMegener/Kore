package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.ItemRegister
import io.kito.kore.common.registry.RegistryTemplate
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile

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
    val itemTemplate = RegistryTemplate { i: String ->
        "${i}_item" of ::Item where {
            named(EN_US to "${i.toTitle()} Item",
                  PT_BR to "Block ${i.toTitle()}")

            model { loc, _ ->
                getBuilder(loc.toString())
                    .parent(UncheckedModelFile("item/generated"))
                    .texture("layer0", local("item/item"))
            }
        }
    }
}

