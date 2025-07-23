package io.kito.kore.common.datagen

import io.kito.kore.Kore.ID
import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.common.registry.EntityTypeRegister.EntityTypeBuilder
import io.kito.kore.common.registry.FluidTypeRegister
import io.kito.kore.common.registry.FluidTypeRegister.FluidTypeBuilder
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.common.world.TagBiomeModifier
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.PlacedFeatureExt.commonOrePlacement
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.BuiltInRegistries.*
import net.minecraft.core.registries.Registries.CONFIGURED_FEATURE
import net.minecraft.core.registries.Registries.PLACED_FEATURE
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.EntityLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.VerticalAnchor.absolute
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement.triangle
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.common.Tags.Biomes.IS_OVERWORLD
import net.neoforged.neoforge.common.conditions.ICondition
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS
import net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES
import net.neoforged.neoforgespi.language.IModInfo
import org.apache.http.client.entity.EntityBuilder
import java.util.function.BiConsumer
import net.minecraft.world.item.CreativeModeTab.Builder as TabBuilder


typealias   ItemModelBuilder<T> =  ItemModelProvider.(ResourceLocation, T) -> Unit
typealias  BlockStateBuilder<T> = BlockStateProvider.(ResourceLocation, T) -> Unit
typealias      RecipeBuilder<T> = (HolderLookup.Provider) -> Recipe<T>
typealias TranslationBuilder = LanguageProvider.() -> Unit

/**
 * Abstract base class for mod-specific data generation helpers in Kore.
 * Subclasses of this helper are responsible for collecting and registering various data providers
 * such as block states, item models, recipes, and language entries for their respective mods.
 * This class integrates with Kore's scanning system to automatically addEntry data providers
 * with NeoForge's data generation event.
 *
 * @property modId The unique identifier of the mod this data generation helper belongs to.
 */
abstract class DataGenHelper(private val modId: String) {

    /**
     * A list of item model builders, each consisting of a [ResourceLocation] and a lambda for building the item model.
     */
    private val  itemModelBuilders = arrayListOf<Pair<ResourceLocation, ItemModelBuilder<Item>>>()
    /**
     * A list of block state builders, each consisting of a [ResourceLocation] and a lambda for building the block state.
     */
    private val blockModelBuilders = arrayListOf<Pair<ResourceLocation, BlockStateBuilder<Block>>>()
    /**
     * A list of recipe builders, each a lambda that takes a [RecipeOutput] and [HolderLookup.Provider] to generate recipes.
     */
    private val     recipeBuilders = arrayListOf<RecipeOutput.(HolderLookup.Provider) -> Unit>()

    /**
     * A map storing translation entries, keyed by locale (e.g., "en_us").
     * Each value is a list of [TranslationBuilder] lambdas for adding translations to a [LanguageProvider].
     */
    private val translationEntries = hashMapOf<String, ArrayList<TranslationBuilder>>()

    private val lootTableSubProviders =
        arrayListOf<Pair<(HolderLookup.Provider) -> LootTableSubProvider, LootContextParamSet>>()

    private var requiredLootTables = setOf<ResourceKey<LootTable>>()

    private val builtInProviders = arrayListOf<Pair<ResourceKey<out Registry<Any>>, (BootstrapContext<Any>) -> Unit>>()

    private val featureTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<ResourceKey<*>, TagKey<*>>>>()
    private val optionalFeatureTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<ResourceLocation, TagKey<*>>>>()
    private val tagTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<TagKey<*>, TagKey<*>>>>()
    private val optionalTagTagProvider = hashMapOf<Registry<*>, ArrayList<() -> Pair<ResourceLocation, TagKey<*>>>>()

    /**
     * A list of custom data providers to be registered, along with their distribution target (client or server).
     */
    val providers = arrayListOf<Pair<Dist, (PackOutput) -> DataProvider>>()

    /**
     * Internal list of data generation blocks (lambdas) collected from functions annotated with [DataGen].
     * These blocks are executed before registering data providers.
     */
    internal val blocks = arrayListOf<() -> Unit>()

    /**
     * Extension function for [BlockBuilder] to define a custom block state for a block.
     * @param T The type of the [Block].
     * @param builder A lambda that takes a [BlockStateProvider], [ResourceLocation], and [Block] to define the block state.
     */
    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.state(builder: BlockStateBuilder<T>)
        { blockModelBuilders += loc(modId, blockName) to (builder as BlockStateBuilder<Block>) }

    /**
     * Extension function for [BlockBuilder] to define a default simple block state for a block.
     * This uses [BlockStateProvider.simpleBlock].
     */
    fun BlockBuilder<*>.defaultState() { state { _, it -> simpleBlock(it) } }
    /**
     * Extension function for [BlockBuilder] to define a default simple block state with an item model for a block.
     * This uses [BlockStateProvider.simpleBlockWithItem] and [BlockStateProvider.cubeAll].
     */
    fun BlockBuilder<*>.defaultStateAndItemModel() { state { _, it -> simpleBlockWithItem(it, cubeAll(it)) } }

    /**
     * Extension function for [BlockBuilder] to add named translation entries for a block.
     * @param entries A vararg of pairs, where each pair is a locale string and the translated name.
     */
    fun BlockBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(BLOCK[blockId], it.second) } } }


    /**
     * Extension function for [ItemBuilder] to define a custom item model for an item.
     * @param T The type of the [Item].
     * @param builder A lambda that takes an [ItemModelProvider], [ResourceLocation], and [Item] to define the item model.
     */
    @Suppress(UNCHECKED_CAST)
    fun <T : Item> ItemBuilder<T>.model(builder: ItemModelBuilder<T>)
        { itemModelBuilders += loc(modId, name)  to (builder as ItemModelBuilder<Item>) }

    /**
     * Extension function for [ItemBuilder] to define a default basic item model.
     * This uses [ItemModelProvider.basicItem].
     */
    fun ItemBuilder<*>.defaultModel() { model { loc, _ -> basicItem(loc) } }

    fun ItemBuilder<*>.optionalDefaultModel() {
        model { loc, _ ->
            existingFileHelper.trackGenerated(loc.item, ModelProvider.TEXTURE)
            basicItem(loc)
        }
    }

    /**
     * Extension function for [ItemBuilder] to define a simple block item model.
     * This uses [ItemModelProvider.simpleBlockItem].
     */
    fun ItemBuilder<*>.blockModel() { model { loc, _ -> simpleBlockItem(loc) } }
    /**
     * Extension function for [ItemBuilder] to define a spawn egg item model.
     * This uses [ItemModelProvider.spawnEggItem].
     */
    fun ItemBuilder<*>.spawnEggModel() { model { loc, _ -> spawnEggItem(loc) } }

    /**
     * Extension function for [ItemBuilder] to define a bucket item model.
     * This sets up the model to use a generic bucket texture with a custom fluid layer.
     */
    fun ItemBuilder<*>.bucketModel() { model { loc, _ ->
        withExistingParent("$loc", mcLoc("item/generated"))
            .texture("layer0", mcLoc("bucket").item)
            .texture("layer1", loc.item)
    } }

    /**
     * Extension function for [ItemBuilder] to add named translation entries for an item.
     * @param entries A vararg of pairs, where each pair is a locale string and the translated name.
     */
    fun ItemBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(ITEM[itemId], it.second) } } }


    fun EntityTypeBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(ENTITY_TYPE[entityTypeId], it.second) } } }

    fun FluidTypeBuilder.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(fluidTypeId.toLanguageKey("fluid_type"), it.second) } } }


    /**
     * Extension function for [TabBuilder] (CreativeModeTab.Builder) to add named translation entries for a creative tab.
     * @param name The name of the creative tab, used to form the resource location.
     * @param entries A vararg of pairs, where each pair is a locale string and the translated name.
     */
    fun TabBuilder.named(name: String, vararg entries: Pair<String, String>)
    { loc(modId, name).toLanguageKey("itemGroup").let { key ->
        title(Component.translatable(key))
        entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } += { add(key, it.second) } }
    } }

    /**
     * Registers a recipe with a given [ResourceLocation] and a [RecipeBuilder].
     * @param T The type of [RecipeInput] for the recipe.
     * @param loc The [ResourceLocation] for the recipe.
     * @param builder A lambda that takes a [HolderLookup.Provider] and returns a [Recipe].
     */
    fun <T : RecipeInput> recipe(loc: ResourceLocation, builder: RecipeBuilder<T>) {
        recipeBuilders += { accept(loc, builder(it), null) }
    }

    /**
     * Registers a recipe with a given [ResourceLocation], an [AdvancementHolder], and a [RecipeBuilder].
     * @param T The type of [RecipeInput] for the recipe.
     * @param loc The [ResourceLocation] for the recipe.
     * @param advancement The [AdvancementHolder] associated with this recipe.
     * @param builder A lambda that takes a [HolderLookup.Provider] and returns a [Recipe].
     */
    fun <T : RecipeInput> recipe(loc: ResourceLocation, advancement: AdvancementHolder, builder: RecipeBuilder<T>) {
        recipeBuilders += { accept(loc, builder(it), advancement) }
    }

    /**
     * Registers a recipe with a given [ResourceLocation], an [AdvancementHolder], optional [ICondition]s, and a [RecipeBuilder].
     * @param T The type of [RecipeInput] for the recipe.
     * @param loc The [ResourceLocation] for the recipe.
     * @param advancement The [AdvancementHolder] associated with this recipe.
     * @param conditions A vararg of [ICondition]s that must be met for the recipe to be active.
     * @param builder A lambda that takes a [HolderLookup.Provider] and returns a [Recipe].
     */
    fun <T : RecipeInput> recipe(loc: ResourceLocation,
                                 advancement: AdvancementHolder,
                                 vararg conditions: ICondition,
                                 builder: RecipeBuilder<T>)
    { recipeBuilders += { accept(loc, builder(it), advancement, *conditions) } }


    @Suppress(UNCHECKED_CAST)
    fun <T : RecipeInput, I : Item> ItemBuilder<I>.recipe(loc: ResourceLocation,
                                                          builder: (HolderLookup.Provider, I) -> Recipe<T>)
    { recipeBuilders += { accept(loc, builder(it, ITEM[itemId] as I), null) } }

    @Suppress(UNCHECKED_CAST)
    fun <T : RecipeInput, I: Item> ItemBuilder<I>.recipe(loc: ResourceLocation,
                                                         advancement: AdvancementHolder,
                                                         builder: (HolderLookup.Provider, I) -> Recipe<T>)
    { recipeBuilders += { accept(loc, builder(it, ITEM[itemId] as I), advancement) } }

    @Suppress(UNCHECKED_CAST)
    fun <T : RecipeInput, I: Item>  ItemBuilder<I>.recipe(loc: ResourceLocation,
                                                          advancement: AdvancementHolder,
                                                          vararg conditions: ICondition,
                                                          builder: (HolderLookup.Provider, I) -> Recipe<T>)
    { recipeBuilders += { accept(loc, builder(it, ITEM[itemId] as I), advancement, *conditions) } }


    fun blockLootTable(block: (HolderLookup.Provider) -> BlockLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to LootContextParamSets.BLOCK
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.blockLootTable(block: (HolderLookup.Provider, T) -> BlockLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it, BLOCK[blockId] as T) } to
                LootContextParamSets.BLOCK
    }

    fun blockLootTable(block: (HolderLookup.Provider) -> BlockLootSubProvider,
                       requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider ->  block(it) } to LootContextParamSets.BLOCK
        requiredLootTables += requiredTables
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.blockLootTable(block: (HolderLookup.Provider, T) -> BlockLootSubProvider,
                                                   requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it, BLOCK[blockId] as T) } to
                LootContextParamSets.BLOCK

        requiredLootTables += requiredTables
    }

    fun entityLootTable(block: (HolderLookup.Provider) -> EntityLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider ->  block(it) } to LootContextParamSets.ENTITY
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Entity> EntityTypeBuilder<T>.entityLootTable(
        block: (HolderLookup.Provider, EntityType<T>) -> EntityLootSubProvider
    ) {
        lootTableSubProviders += { it: HolderLookup.Provider ->
            block(it, ENTITY_TYPE[entityTypeId] as EntityType<T>)
        } to LootContextParamSets.ENTITY
    }

    fun entityLootTable(block: (HolderLookup.Provider) -> EntityLootSubProvider,
                        requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to LootContextParamSets.ENTITY
        requiredLootTables += requiredTables
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Entity> EntityTypeBuilder<T>.entityLootTable(
        block: (HolderLookup.Provider, EntityType<T>) -> EntityLootSubProvider,
        requiredTables: Set<ResourceKey<LootTable>>
    ) {
        lootTableSubProviders += { it: HolderLookup.Provider ->
            block(it, ENTITY_TYPE[entityTypeId] as EntityType<T>)
        } to LootContextParamSets.ENTITY

        requiredLootTables += requiredTables
    }

    fun lootTable(ctx: LootContextParamSet,
                  block: (HolderLookup.Provider) -> LootTableSubProvider)
    { lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to ctx }

    fun lootTable(ctx: LootContextParamSet,
                  requiredTables: Set<ResourceKey<LootTable>>,
                  block: (HolderLookup.Provider) -> LootTableSubProvider)
    {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to ctx
        requiredLootTables += requiredTables
    }

    @Suppress(UNCHECKED_CAST)
    fun <T> ResourceKey<out Registry<T>>.provider(bootstrap: (BootstrapContext<T>) -> Unit) {
        builtInProviders +=
            ((this to bootstrap) as Pair<ResourceKey<out Registry<Any>>, (BootstrapContext<Any>) -> Unit>)
    }

    fun oreConfiguration(oreState: BlockState, id: ResourceLocation, rule: RuleTest, size: Int) :
            ResourceKey<ConfiguredFeature<*, *>>
    {
        val key = ResourceKey.create(CONFIGURED_FEATURE, id)

        CONFIGURED_FEATURE.provider { ctx ->
            FeatureUtils.register(
                ctx, key, Feature.ORE,
                OreConfiguration(rule, oreState, size)
            )
        }

        return key
    }


    fun BlockBuilder<*>.oreConfiguration(id: ResourceLocation, rule: RuleTest, size: Int) :
            ResourceKey<ConfiguredFeature<*, *>>
    {
        val key = ResourceKey.create(CONFIGURED_FEATURE, id)

        CONFIGURED_FEATURE.provider { ctx ->
            FeatureUtils.register(
                ctx, key, Feature.ORE,
                OreConfiguration(rule, BLOCK[blockId].defaultBlockState(), size)
            )
        }

        return key
    }

    fun BlockBuilder<*>.oreConfiguration(rule: RuleTest, size: Int): ResourceKey<ConfiguredFeature<*, *>> =
        oreConfiguration(blockId.withSuffix("_ore_config"), rule, size)

    fun BlockBuilder<*>.stoneOreConfiguration(id: ResourceLocation, size: Int): ResourceKey<ConfiguredFeature<*, *>> =
        oreConfiguration(id, TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), size)

    fun BlockBuilder<*>.deepslateOreConfiguration(id: ResourceLocation, size: Int): ResourceKey<ConfiguredFeature<*, *>>
        = oreConfiguration(id, TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), size)

    fun BlockBuilder<*>.stoneOreConfiguration(size: Int): ResourceKey<ConfiguredFeature<*, *>> =
        stoneOreConfiguration(blockId.withSuffix("_ore_config"), size)

    fun BlockBuilder<*>.deepslateOreConfiguration(size: Int): ResourceKey<ConfiguredFeature<*, *>> =
        deepslateOreConfiguration(blockId.withSuffix("_ore_config"), size)

    fun placedFeature(id: ResourceLocation,
                      oreConfig: ResourceKey<ConfiguredFeature<*, *>>,
                      placementModfiers: List<PlacementModifier>): ResourceKey<PlacedFeature>
    {
        val key = ResourceKey.create(PLACED_FEATURE, id)

        PLACED_FEATURE.provider { ctx ->
            PlacementUtils.register(ctx, key, ctx.lookup(CONFIGURED_FEATURE).getOrThrow(oreConfig), placementModfiers)
        }

        return key
    }

    fun BlockBuilder<*>.placedFeature(oreConfig: ResourceKey<ConfiguredFeature<*, *>>,
                                      placementModfiers: List<PlacementModifier>): ResourceKey<PlacedFeature> =
        placedFeature(blockId, oreConfig, placementModfiers)

    fun biomeModifier(id: ResourceLocation, modifier: (BootstrapContext<BiomeModifier>) -> BiomeModifier):
            ResourceKey<BiomeModifier>
    {
        val key = ResourceKey.create(BIOME_MODIFIERS, id)

        BIOME_MODIFIERS.provider { ctx ->
            ctx.register(key, modifier(ctx))
        }

        return key
    }

    fun BlockBuilder<*>.biomeModifier(modifier: (BootstrapContext<BiomeModifier>) -> BiomeModifier) =
        biomeModifier(blockId, modifier)

    fun BlockBuilder<*>.tagBiomeModifier(tag: TagKey<Biome>,
                                         feature: ResourceKey<PlacedFeature>,
                                         step: GenerationStep.Decoration) =
        biomeModifier { ctx ->
            TagBiomeModifier(
                tag,
                HolderSet.direct(
                    ctx.lookup(PLACED_FEATURE).getOrThrow(feature)
                ),
                step
            )
        }

    fun BlockBuilder<*>.oreTagBiomeModifier(tag: TagKey<Biome>, feature: ResourceKey<PlacedFeature>) =
        tagBiomeModifier(tag, feature, UNDERGROUND_ORES)

    fun BlockBuilder<*>.overworldOreTagBiomeModifier(feature: ResourceKey<PlacedFeature>) =
        tagBiomeModifier(IS_OVERWORLD, feature, UNDERGROUND_ORES)

    fun <T : Any> Registry<T>.addTag(feature: () -> T, vararg tags: TagKey<T>) {
        featureTagProviders.computeIfAbsent(this) { arrayListOf() } +=
            tags.map { { ResourceKey.create(key(), getKey(feature())!!) to it } }
    }

    fun <T : Any> Registry<T>.addTag(tag: TagKey<T>, vararg tags: TagKey<T>) {
        tagTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { tag to it } }
    }

    fun <T : Any> Registry<T>.addOptionalToTag(id: ResourceLocation, vararg tags: TagKey<T>) {
        optionalFeatureTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { id to it } }
    }

    fun <T : Any> Registry<T>.addOptionalTagToTag(id: ResourceLocation, vararg tags: TagKey<T>) {
        optionalTagTagProvider.computeIfAbsent(this) { arrayListOf() } += tags.map { { id to it } }
    }

    fun ItemBuilder<*>.tags(vararg tags: TagKey<Item>) {
        featureTagProviders.computeIfAbsent(ITEM) { arrayListOf() } +=
            tags.map { { ResourceKey.create(ITEM.key(), itemId) to it } }
    }

    fun BlockBuilder<*>.tags(vararg tags: TagKey<Block>) {
        featureTagProviders.computeIfAbsent(BLOCK) { arrayListOf() } +=
            tags.map { { ResourceKey.create(BLOCK.key(), blockId) to it } }
    }

    fun EntityTypeBuilder<*>.tags(vararg tags: TagKey<EntityType<*>>) {
        featureTagProviders.computeIfAbsent(ENTITY_TYPE) { arrayListOf() } +=
            tags.map { { ResourceKey.create(ENTITY_TYPE.key(), entityTypeId) to it } }
    }

    fun FluidTypeBuilder.tags(vararg tags: TagKey<FluidTypeBuilder>) {
        featureTagProviders.computeIfAbsent(FLUID_TYPES) { arrayListOf() } +=
            tags.map { { ResourceKey.create(FLUID_TYPES.key(), fluidTypeId) to it } }
    }


    fun <T : Any> Registry<T>.addTag(feature: () -> T, vararg tags: () -> TagKey<T>) {
        featureTagProviders.computeIfAbsent(this) { arrayListOf() } +=
            tags.map { { ResourceKey.create(key(), getKey(feature())!!) to it() } }
    }

    fun <T : Any> Registry<T>.addTag(tag: TagKey<T>, vararg tags: () -> TagKey<T>) {
        tagTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { tag to it() } }
    }

    fun <T : Any> Registry<T>.addOptionalToTag(id: ResourceLocation, vararg tags: () -> TagKey<T>) {
        optionalFeatureTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { id to it() } }
    }

    fun <T : Any> Registry<T>.addOptionalTagToTag(id: ResourceLocation, vararg tags: () -> TagKey<T>) {
        optionalTagTagProvider.computeIfAbsent(this) { arrayListOf() } += tags.map { { id to it() } }
    }

    fun ItemBuilder<*>.tags(vararg tags: () -> TagKey<Item>) {
        featureTagProviders.computeIfAbsent(ITEM) { arrayListOf() } +=
            tags.map { { ResourceKey.create(ITEM.key(), itemId) to it() } }
    }

    fun BlockBuilder<*>.tags(vararg tags: () -> TagKey<Block>) {
        featureTagProviders.computeIfAbsent(BLOCK) { arrayListOf() } +=
            tags.map { { ResourceKey.create(BLOCK.key(), blockId) to it() } }
    }

    fun EntityTypeBuilder<*>.tags(vararg tags: () -> TagKey<EntityType<*>>) {
        featureTagProviders.computeIfAbsent(ENTITY_TYPE) { arrayListOf() } +=
            tags.map { { ResourceKey.create(ENTITY_TYPE.key(), entityTypeId) to it() } }
    }

    fun FluidTypeBuilder.tags(vararg tags: () -> TagKey<FluidTypeBuilder>) {
        featureTagProviders.computeIfAbsent(FLUID_TYPES) { arrayListOf() } +=
            tags.map { { ResourceKey.create(FLUID_TYPES.key(), fluidTypeId) to it() } }
    }

    /**
     * Registers all collected data providers with the [GatherDataEvent].
     * This method is typically called by NeoForge during the data generation phase.
     *
     * @param event The [GatherDataEvent] provided by NeoForge.
     */
    fun register(event: GatherDataEvent) {
        // Execute all collected data generation blocks.
        blocks.forEach { it() }

        val generator = event.generator

        // Add DynamicBlockStateProvider for block states.
        generator.addProvider(
            event.includeClient(),
            DynamicBlockStateProvider(generator.packOutput, modId, event.existingFileHelper,
                blockModelBuilders.map { { p -> it.second(p, it.first, BLOCK[it.first]) } })
        )
        // Add DynamicItemModelProvider for item models.
        generator.addProvider(
            event.includeClient(),
            DynamicItemModelProvider(generator.packOutput, modId, event.existingFileHelper,
                                     itemModelBuilders.map { { p -> it.second(p, it.first, ITEM[it.first]) } })
        )

        // Add DynamicLanguageProvider for each locale with collected translation entries.
        translationEntries.forEach { (locale, entries) ->
            generator.addProvider(event.includeClient(),
                                  DynamicLanguageProvider(generator.packOutput, modId, locale, entries)
            )
        }

        // Add DynamicRecipeProvider for recipes.
        generator.addProvider(event.includeServer(),
            DynamicRecipeProvider(generator.packOutput, event.lookupProvider, recipeBuilders))

        generator.addProvider(event.includeServer(),
            LootTableProvider(
                generator.packOutput,
                requiredLootTables,
                lootTableSubProviders.map { (s, ctx) -> SubProviderEntry(s, ctx) },
                event.lookupProvider
            )
        )


        generator.addProvider(
            event.includeServer(),
            DatapackBuiltinEntriesProvider(
                generator.packOutput,
                event.lookupProvider,
                RegistrySetBuilder().apply {
                    builtInProviders.groupBy { it.first }
                        .mapValues { it.value.map { pair -> pair.second } }
                        .forEach  { (k, s) -> add(k) { ctx -> s.forEach { it(ctx) } } }
                },
                mutableSetOf(modId)
            )
        )



        featureTagProviders.forEach { (registry, it) ->
            generator.addProvider(
                event.includeServer(),
                DynamicTagProvider(
                    registry.key() as ResourceKey<out Registry<Any>>,
                    it as List<() -> Pair<ResourceKey<Any>, TagKey<Any>>>,
                    arrayListOf(),
                    arrayListOf(),
                    arrayListOf(),
                    generator.packOutput,
                    event.lookupProvider,
                    modId,
                    event.existingFileHelper
                )
            )
        }
        optionalFeatureTagProviders.forEach { (registry, it) ->
            generator.addProvider(
                event.includeServer(),
                DynamicTagProvider(
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    it as List<() -> Pair<ResourceLocation, TagKey<Any>>>,
                    arrayListOf(),
                    arrayListOf(),
                    generator.packOutput,
                    event.lookupProvider,
                    modId,
                    event.existingFileHelper
                )
            )
        }
        tagTagProviders.forEach { (registry, it) ->
            generator.addProvider(
                event.includeServer(),
                DynamicTagProvider(
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    arrayListOf(),
                    it as List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                    arrayListOf(),
                    generator.packOutput,
                    event.lookupProvider,
                    modId,
                    event.existingFileHelper
                )
            )
        }
        optionalTagTagProvider.forEach { (registry, it) ->
            generator.addProvider(
                event.includeServer(),
                DynamicTagProvider(
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    arrayListOf(),
                    arrayListOf(),
                    it as List<() -> Pair<ResourceLocation, TagKey<Any>>>,
                    generator.packOutput,
                    event.lookupProvider,
                    modId,
                    event.existingFileHelper
                )
            )
        }

        // Add any custom data providers.
        providers.forEach { (d, c) ->
            generator.addProvider(when (d) {
                Dist.CLIENT -> event.includeClient()
                Dist.DEDICATED_SERVER -> event.includeServer()
            }, c(generator.packOutput))
        }
    }

    /**
     * Companion object responsible for scanning and attaching [DataGenHelper] instances to the [GatherDataEvent].
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {

        /**
         * Scans for objects that extend [DataGenHelper] and attaches their `addEntry` method
         * to the [GatherDataEvent] for the corresponding mod.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [DataGenHelper] instance to be attached.
         */
        @ObjectScanner(DataGenHelper::class)
        fun attachDataGenHelperToEvent(info: IModInfo, container: ModContainer, data: DataGenHelper) {
            // Only attach if the mod ID matches the DataGenHelper's mod ID.
            if (info.modId != data.modId) return

            // Add a listener to the mod's event bus for the GatherDataEvent.
            container.eventBus?.addListener(data::register)
        }
    }
}

