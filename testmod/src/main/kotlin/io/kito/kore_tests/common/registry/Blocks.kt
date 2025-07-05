package io.kito.kore_tests.common.registry

import io.kito.kore.common.event.RegisterTemplate
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister
import io.kito.kore.common.template.Template.Companion.include
import io.kito.kore.common.world.TagBiomeModifier
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.PlacedFeatureExt.commonOrePlacement
import io.kito.kore.util.neoforge.Capability.blockItemHandler
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.blockLootTable
import io.kito.kore_tests.DataGenerator.blockModel
import io.kito.kore_tests.DataGenerator.defaultState
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.DataGenerator.oreConfiguration
import io.kito.kore_tests.DataGenerator.provider
import io.kito.kore_tests.DataGenerator.state
import io.kito.kore_tests.DataGenerator.stoneOreConfiguration
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.registry.Items.itemTemplate
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import io.kito.kore_tests.common.registry.early.Strings.myGroup
import io.kito.kore_tests.common.world.level.block.CustomBlock
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.core.registries.Registries.CONFIGURED_FEATURE
import net.minecraft.core.registries.Registries.PLACED_FEATURE
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.client.model.generators.ModelFile
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement.triangle
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS
import net.neoforged.neoforge.common.Tags.Biomes.IS_OVERWORLD
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES

/**
 * Registers all custom blocks for the Kore Tests mod.
 * Annotated with `@Scan` to be automatically discovered by Kore for block registration.
 * Extends `BlockRegister` with the mod ID, providing a DSL for defining blocks.
 */
@Scan
object Blocks : BlockRegister(ID) {

    /**
     * Defines a custom block named "block".
     * - `of ::CustomBlock`: Specifies the block class `CustomBlock`.
     * - `named`: Sets the localized names for the block (English and Brazilian Portuguese).
     * - `state`: Defines the block state generation using `simpleBlock`.
     * - `props`: Sets block properties like `explosionResistance`.
     * - `defaultItem`: Configures the default item form of the block, including its model and item properties.
     * - `blockEntity`: Associates a `CustomBlockEntity` with this block and defines its capabilities (e.g., item handling).
     */
    val block by "block" of ::CustomBlock where {
        named(EN_US to "Block",
              PT_BR to "Bloco")

        defaultState()

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

    val ore by "ore" of ::CustomBlock where {
        named(EN_US to "Ore",
              PT_BR to "Minério")

        defaultState()

        props { explosionResistance(1000f) }

        defaultItem {
            blockModel()
            props {
                stacksTo(1)
            }
        }

        val oreConfig = stoneOreConfiguration(9)

        val orePlacedFeature = ResourceKey.create(PLACED_FEATURE, blockId)

        PLACED_FEATURE.provider(LogicalSide.SERVER) { ctx ->
            PlacementUtils.register(
                ctx,
                ResourceKey.create(PLACED_FEATURE, blockId),
                ctx.lookup(CONFIGURED_FEATURE).getOrThrow(oreConfig),
                commonOrePlacement(2, triangle(VerticalAnchor.absolute(16), VerticalAnchor.absolute(32)))
            )
        }

        BIOME_MODIFIERS.provider(LogicalSide.SERVER) { ctx ->
            ctx.register(
                ResourceKey.create(BIOME_MODIFIERS, blockId),
                TagBiomeModifier(
                    IS_OVERWORLD,
                    HolderSet.direct(ctx.lookup(PLACED_FEATURE).getOrThrow(orePlacedFeature)),
                    UNDERGROUND_ORES
                )
            )
        }
    }

    /**
     * A template for defining similar blocks programmatically.
     * It takes a string `i` to generate unique block names and localized names.
     * - `of ::Block`: Uses the generic `Block` class.
     * - `state`: Defines a simple block state with `cubeAll` using the base "block" model.
     * - `props`: Sets common block properties.
     * - `defaultItem`: Configures the item model to reference the base "block" model.
     */
    @RegisterTemplate
    val blockTemplate = blockTemplate(stringRegistry) { i: String ->
        "${i}_block" of ::Block where {
            named(EN_US to "${i.toTitle()} Block",
                  PT_BR to "Bloco ${i.toTitle()}")

            state { _, b -> simpleBlock(b, cubeAll(block)) }

            props { explosionResistance(1000f) }

            defaultItem {
                model { l, _ ->
                    getBuilder(l.toString()).parent(ModelFile.UncheckedModelFile("kore_tests:block/block"))
                }
            }

            blockLootTable { lookup, block ->
                object : BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), lookup) {
                    override fun generate() {
                        add(block, createSingleItemTable(itemTemplate[i]!!, ConstantValue(9f)))
                    }

                    override fun getKnownBlocks() = mutableListOf(block)
                }
            }
        }
    }.include(myGroup)

    /**
     * Lazily initialized property to get the `blockItem` associated with the `block`.
     */
    val blockItem   by Blocks::block.blockItem
    /**
     * Lazily initialized property to get the `blockEntity` associated with the `block`.
     */
    val blockEntity by Blocks::block.blockEntity
}

