package io.kito.kore_tests.common.registry

import io.kito.kore.common.event.RegisterTemplate
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister
import io.kito.kore.common.template.TagTemplate
import io.kito.kore.common.template.Template.Companion.include
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.PlacedFeatureExt.commonOrePlacement
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.neoforge.Capability.blockItemHandler
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.blockLootTable
import io.kito.kore_tests.DataGenerator.cubeAllModel
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.overworldOreTagBiomeModifier
import io.kito.kore_tests.DataGenerator.placedFeature
import io.kito.kore_tests.DataGenerator.stoneOreConfiguration
import io.kito.kore_tests.DataGenerator.tags
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.Items.itemTemplate
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import io.kito.kore_tests.common.registry.early.Strings.myGroup
import io.kito.kore_tests.common.world.level.block.CustomBlock
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.data.models.model.TexturedModel
import net.minecraft.core.registries.Registries.BLOCK
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.VerticalAnchor.absolute
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement.triangle
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@Scan
object Blocks : BlockRegister(ID) {

    val myTag = TagKey.create(BLOCK, local("my_tag"))

    @RegisterTemplate
    val myTagTemplate = TagTemplate(stringRegistry) { TagKey.create(BLOCK, local("$it/my_tag")) }.include(myGroup)


    val block by "block" of ::CustomBlock where {
        named(EN_US to "Block",
              PT_BR to "Bloco")

        cubeAllModel()

        props { explosionResistance(1000f) }

        defaultItem {
            props {
                stacksTo(1)
            }
        }

        blockEntity(::CustomBlockEntity) {
            withCaps { blockItemHandler { inventory } }
        }
    }

    val ore by "ore" of ::CustomBlock where {
        named(EN_US to "Ore",
              PT_BR to "Minério")

        cubeAllModel()

        props { explosionResistance(1000f) }

        defaultItem {
            props {
                stacksTo(1)
            }
        }

        overworldOreTagBiomeModifier(
            placedFeature(
                stoneOreConfiguration(9),
                commonOrePlacement(2, triangle(absolute(16), absolute(32)))
            )
        )

        tags(myTag)
    }


    @RegisterTemplate
    val blockTemplate = blockTemplate(stringRegistry) { i: String ->
        "${i}_block" of ::Block where {
            named(EN_US to "${i.toTitle()} Block",
                  PT_BR to "Bloco ${i.toTitle()}")

            model { createTrivialBlock(it) { TexturedModel.createAllSame(local("block").block) } }

            props { explosionResistance(1000f) }

            /*blockLootTable { lookup, block ->
                object : BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), lookup) {
                    override fun generate() {
                        add(block, createSingleItemTable(itemTemplate[i]!!, ConstantValue(9f)))
                    }

                    override fun getKnownBlocks() = mutableListOf(block)
                }
            }*/

            overworldOreTagBiomeModifier(
                placedFeature(
                    stoneOreConfiguration(9),
                    commonOrePlacement(2, triangle(absolute(16), absolute(32)))
                )
            )

            tags({ myTagTemplate[i]!! })
        }
    }.include(myGroup)


    val blockItem   by Blocks::block.blockItem

    val blockEntity by Blocks::block.blockEntity
}

