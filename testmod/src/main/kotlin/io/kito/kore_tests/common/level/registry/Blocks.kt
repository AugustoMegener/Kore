package io.kito.kore_tests.common.level.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister
import io.kito.kore.util.EN_US
import io.kito.kore.util.PT_BR
import io.kito.kore.util.neoforge.Capability.blockItemHandler
import io.kito.kore_tests.common.level.block.entity.CustomBlockEntity
import io.kito.kore_tests.DataGenerator.blockModel
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.state
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.level.block.CustomBlock
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object Blocks : BlockRegister(ID) {

    val block by "block" of ::CustomBlock where {
        named(EN_US to "Block",
              PT_BR to "Bloco")

        state { _, b -> simpleBlock(b) }

        props { explosionResistance(1000f) }

        defaultItem {
            blockModel()
            props {
                stacksTo(1)
            }


        }


        blockEntity(::CustomBlockEntity) {
            withCaps { blockItemHandler { inventory } }
        }
    }

    val blockItem   by Blocks::block.blockItem
    val blockEntity by Blocks::block.blockEntity
}