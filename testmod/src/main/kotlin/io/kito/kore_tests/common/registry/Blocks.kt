package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.neoforge.Capability.blockItemHandler
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.blockModel
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.state
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.world.level.block.CustomBlock
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.ModelFile
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

    val blockTemplate = blockTemplate { i: String ->
        "${i}_block" of ::Block where {
            named(EN_US to "${i.toTitle()} Block",
                  PT_BR to "Bloco ${i.toTitle()}")

            state { _, b -> simpleBlock(b, cubeAll(block)) }

            props { explosionResistance(1000f) }

            defaultItem {
                model { l, _ ->
                    getBuilder(l.toString()).parent(ModelFile.UncheckedModelFile("kore_tests:block/block"))
                }
                props {
                    stacksTo(1)
                }
            }
        }
    }

    val blockItem   by Blocks::block.blockItem
    val blockEntity by Blocks::block.blockEntity
}