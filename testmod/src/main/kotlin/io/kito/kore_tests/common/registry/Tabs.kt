package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.CreativeModeTabRegister
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.registry.Blocks.blockTemplate
import io.kito.kore_tests.common.registry.Items.itemTemplate
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object Tabs : CreativeModeTabRegister(ID) {
    val myTab by "tab" where {
        icon { Blocks.blockItem.defaultInstance }

        named(it, EN_US to "My Tab",
                  PT_BR to "Minha Aba")

        display { _ ->
            items(Blocks::blockItem, EntityTypes::myMobSpawnEgg)
            templates(blockTemplate)
            templates(itemTemplate)
        }
    }
}