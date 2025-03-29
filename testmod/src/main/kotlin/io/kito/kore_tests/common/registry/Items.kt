package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.ItemRegister
import io.kito.kore.common.registry.RegistryTemplate
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.neoforge.ResourceLocationExt.item
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile

@Scan
object Items : ItemRegister(ID) {
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