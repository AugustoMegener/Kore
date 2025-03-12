package io.kito.kore_tests.common.level.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.CreativeModeTabRegister
import io.kito.kore.util.EN_US
import io.kito.kore.util.PT_BR
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object Tabs : CreativeModeTabRegister(ID) {
    val myTab by "tab" where {
        icon { Blocks.blockItem.defaultInstance }

        named(it, EN_US to "My Tab",
                  PT_BR to "Minha Aba")

        items(Blocks::blockItem, EntityTypes::myMobSpawnEgg)
    }
}