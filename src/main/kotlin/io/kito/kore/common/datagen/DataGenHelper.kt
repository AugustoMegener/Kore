package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.common.registry.EntityTypeRegister.EntityTypeBuilder
import io.kito.kore.common.registry.FluidTypeRegister.FluidTypeBuilder
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.common.world.TagBiomeModifier
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelLocationUtils
import net.minecraft.client.data.models.model.ModelTemplates.FLAT_ITEM
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TexturedModel
import net.minecraft.client.data.models.model.TexturedModel.createAllSame
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
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.network.chat.Component.translatable
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
import net.minecraft.world.level.storage.loot.LootTable
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.common.Tags.Biomes.IS_OVERWORLD
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import net.minecraft.world.item.CreativeModeTab.Builder as TabBuilder


typealias TranslationBuilder = LanguageProvider.() -> Unit

abstract class DataGenHelper(private val modId: String) {

    private val tagRegistries = hashMapOf<KClass<*>, Registry<Any>>()

    private val blockGenerators = arrayListOf<Pair<ResourceLocation, BlockModelGenerators.(Block) -> Unit>>()
    private val itemGenerators = arrayListOf<Pair<ResourceLocation, ItemModelGenerators.(Item) -> Unit>>()

    private val     recipeBuilders = arrayListOf<(RecipeOutput, HolderLookup.Provider, RecipeProvider) -> Unit>()

    private var requiredLootTables = setOf<ResourceKey<LootTable>>()

    private val lootTableSubProviders =
        arrayListOf<Pair<(HolderLookup.Provider) -> LootTableSubProvider, ContextKeySet>>()

    private val builtInProviders = arrayListOf<Pair<ResourceKey<out Registry<Any>>, (BootstrapContext<Any>) -> Unit>>()

    private val featureTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<Any, TagKey<*>>>>()
    private val optionalFeatureTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<Any, TagKey<*>>>>()
    private val tagTagProviders = hashMapOf<Registry<*>, ArrayList<() -> Pair<TagKey<*>, TagKey<*>>>>()
    private val optionalTagTagProvider = hashMapOf<Registry<*>, ArrayList<() -> Pair<TagKey<*>, TagKey<*>>>>()

    private val translationEntries = hashMapOf<String, ArrayList<TranslationBuilder>>()

    val providers = arrayListOf<(PackOutput) -> DataProvider>()

    internal val blocks = arrayListOf<() -> Unit>()


    fun BlockBuilder<*>.model(mappings: BlockModelGenerators.(Block) -> Unit) {
        blockGenerators += blockId to mappings
    }

    fun ItemBuilder<*>.model(mappings: ItemModelGenerators.(Item) -> Unit) {
        itemGenerators += itemId to mappings
    }

    fun ItemBuilder<*>.flatModel() {
        model { generateFlatItem(it, FLAT_ITEM) }
    }

    fun ItemBuilder<*>.flatModel(location: ResourceLocation) {
        model {
            itemModelOutput.accept(
                it,
                ItemModelUtils.plainModel(
                    FLAT_ITEM.create(
                        ModelLocationUtils.getModelLocation(it), TextureMapping.layer0(location.item), modelOutput
                    )
                )
            )
        }
    }



    fun BlockBuilder<*>.cubeAllModel() {
        model { createTrivialCube(it) }
    }

    fun BlockBuilder<*>.cubeAllModel(location: ResourceLocation) {
        model { block ->
            blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(
                    block,
                    BlockModelGenerators.plainVariant(createAllSame(location).create(block, modelOutput))
                )
            )
        }
    }

    fun BlockBuilder<*>.cubeModel() {
        model { createGenericCube(it) }
    }

    fun BlockBuilder<*>.logModel(provider: TexturedModel.Provider) {
        model { createAxisAlignedPillarBlock(it, provider) }
    }

    fun ItemBuilder<*>.defaultModel() {
        model { generateFlatItem(it, FLAT_ITEM); }
    }


    fun BlockBuilder<*>.named(vararg entries: Pair<String, String>)
    { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
        { add(BLOCK[blockId].get().value(), it.second) } } }

    fun ItemBuilder<*>.named(vararg entries: Pair<String, String>)
    { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
        { add(ITEM[itemId].get().value(), it.second) } } }


    fun EntityTypeBuilder<*>.named(vararg entries: Pair<String, String>)
    { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
        { add(ENTITY_TYPE[entityTypeId].get().value(), it.second) } } }

    fun FluidTypeBuilder.named(vararg entries: Pair<String, String>)
    { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
        { add(fluidTypeId.toLanguageKey("fluid_type"), it.second) } } }


    fun TabBuilder.named(name: String, vararg entries: Pair<String, String>)
    { loc(modId, name).toLanguageKey("itemGroup").let { key ->
        title(translatable(key))
        entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } += { add(key, it.second) } }
    } }

    fun recipe(id: ResourceLocation, builder: HolderLookup.Provider.(RecipeProvider) -> RecipeBuilder) {
        recipeBuilders += { output, provider, rProvider -> builder(provider, rProvider).save(output, id.toString()) }
    }

    fun <T : Item> ItemBuilder<T>.recipe(id: ResourceLocation, builder: HolderLookup.Provider.(T, RecipeProvider) -> RecipeBuilder) {
        recipeBuilders += { output, provider, rProvider ->
            builder(provider, ITEM[itemId].get().value() as T, rProvider).save(output, id.toString())
        }
    }

    fun <T : Item> ItemBuilder<T>.recipe(builder: HolderLookup.Provider.(T, RecipeProvider) -> RecipeBuilder) {
        recipeBuilders += { output, provider, rProvider -> builder(provider, ITEM[itemId].get().value() as T, rProvider).save(output) }
    }

    fun blockLootTable(block: (HolderLookup.Provider) -> BlockLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to ContextKeySet.EMPTY
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.blockLootTable(block: (HolderLookup.Provider, T) -> BlockLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it, BLOCK[blockId].get().value() as T) } to
                ContextKeySet.EMPTY
    }

    fun blockLootTable(block: (HolderLookup.Provider) -> BlockLootSubProvider,
                       requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider ->  block(it) } to ContextKeySet.EMPTY
        requiredLootTables += requiredTables
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.blockLootTable(block: (HolderLookup.Provider, T) -> BlockLootSubProvider,
                                                   requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it, BLOCK[blockId] as T) } to
                ContextKeySet.EMPTY

        requiredLootTables += requiredTables
    }

    fun entityLootTable(block: (HolderLookup.Provider) -> EntityLootSubProvider) {
        lootTableSubProviders += { it: HolderLookup.Provider ->  block(it) } to ContextKeySet.EMPTY
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Entity> EntityTypeBuilder<T>.entityLootTable(
        block: (HolderLookup.Provider, EntityType<T>) -> EntityLootSubProvider
    ) {
        lootTableSubProviders += { it: HolderLookup.Provider ->
            block(it, ENTITY_TYPE[entityTypeId].get().value() as EntityType<T>)
        } to ContextKeySet.EMPTY
    }

    fun entityLootTable(block: (HolderLookup.Provider) -> EntityLootSubProvider,
                        requiredTables: Set<ResourceKey<LootTable>>)
    {
        lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to ContextKeySet.EMPTY
        requiredLootTables += requiredTables
    }

    @Suppress(UNCHECKED_CAST)
    fun <T : Entity> EntityTypeBuilder<T>.entityLootTable(
        block: (HolderLookup.Provider, EntityType<T>) -> EntityLootSubProvider,
        requiredTables: Set<ResourceKey<LootTable>>
    ) {
        lootTableSubProviders += { it: HolderLookup.Provider ->
            block(it, ENTITY_TYPE[entityTypeId] as EntityType<T>)
        } to ContextKeySet.EMPTY

        requiredLootTables += requiredTables
    }

    fun lootTable(ctx: ContextKeySet,
                  block: (HolderLookup.Provider) -> LootTableSubProvider)
    { lootTableSubProviders += { it: HolderLookup.Provider -> block(it) } to ctx }

    fun lootTable(ctx: ContextKeySet,
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
                OreConfiguration(rule, BLOCK[blockId].orElseThrow().value().defaultBlockState(), size)
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
            tags.map { { feature() to it } }
    }

    fun <T : Any> Registry<T>.addTag(tag: TagKey<T>, vararg tags: TagKey<T>) {
        tagTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { tag to it } }
    }

    fun <T : Any> Registry<T>.addOptionalToTag(thing: () -> T, vararg tags: TagKey<T>) {
        optionalFeatureTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { thing() to it } }
    }

    fun <T : Any> Registry<T>.addOptionalTagToTag(id: TagKey<T>, vararg tags: TagKey<T>) {
        optionalTagTagProvider.computeIfAbsent(this) { arrayListOf() } += tags.map { { id to it } }
    }

    fun ItemBuilder<*>.tags(vararg tags: TagKey<Item>) {
        featureTagProviders.computeIfAbsent(ITEM) { arrayListOf() } +=
            tags.map { { ITEM[itemId].get().value() to it } }
    }

    fun BlockBuilder<*>.tags(vararg tags: TagKey<Block>) {
        featureTagProviders.computeIfAbsent(BLOCK) { arrayListOf() } +=
            tags.map { { BLOCK[blockId].get().value() to it } }
    }

    fun EntityTypeBuilder<*>.tags(vararg tags: TagKey<EntityType<*>>) {
        featureTagProviders.computeIfAbsent(ENTITY_TYPE) { arrayListOf() } +=
            tags.map { { ENTITY_TYPE[entityTypeId].get().value() to it } }
    }

    fun FluidTypeBuilder.tags(vararg tags: TagKey<FluidTypeBuilder>) {
        featureTagProviders.computeIfAbsent(FLUID_TYPES) { arrayListOf() } +=
            tags.map { { FLUID_TYPES[fluidTypeId].get().value() to it } }
    }


    fun <T : Any> Registry<T>.addTag(feature: () -> T, vararg tags: () -> TagKey<T>) {
        featureTagProviders.computeIfAbsent(this) { arrayListOf() } +=
            tags.map { { feature() to it() } }
    }

    fun <T : Any> Registry<T>.addTag(tag: TagKey<T>, vararg tags: () -> TagKey<T>) {
        tagTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { tag to it() } }
    }

    fun <T : Any> Registry<T>.addOptionalToTag(feature: () -> T, vararg tags: () -> TagKey<T>) {
        optionalFeatureTagProviders.computeIfAbsent(this) { arrayListOf() } += tags.map { { feature() to it() } }
    }

    fun <T : Any> Registry<T>.addOptionalTagToTag(id: () -> TagKey<T>, vararg tags: () -> TagKey<T>) {
        optionalTagTagProvider.computeIfAbsent(this) { arrayListOf() } += tags.map { { id() to it() } }
    }

    fun ItemBuilder<*>.tags(vararg tags: () -> TagKey<Item>) {
        featureTagProviders.computeIfAbsent(ITEM) { arrayListOf() } +=
            tags.map { { ITEM[itemId].get().value() to it() } }
    }

    fun BlockBuilder<*>.tags(vararg tags: () -> TagKey<Block>) {
        featureTagProviders.computeIfAbsent(BLOCK) { arrayListOf() } +=
            tags.map { { BLOCK[blockId].get().value() to it() } }
    }

    fun EntityTypeBuilder<*>.tags(vararg tags: () -> TagKey<EntityType<*>>) {
        featureTagProviders.computeIfAbsent(ENTITY_TYPE) { arrayListOf() } +=
            tags.map { { ENTITY_TYPE[entityTypeId].get().value() to it() } }
    }

    fun FluidTypeBuilder.tags(vararg tags: () -> TagKey<FluidTypeBuilder>) {
        featureTagProviders.computeIfAbsent(FLUID_TYPES) { arrayListOf() } +=
            tags.map { { FLUID_TYPES[fluidTypeId].get().value() to it() } }
    }

    fun registerClient(event: GatherDataEvent.Client) {

        event.createProvider { DynamicItemModelProvider(modId, it, blockGenerators, itemGenerators) }

        event.createProvider {
            LootTableProvider(
                it,
                requiredLootTables,
                lootTableSubProviders.map { (s, ctx) -> LootTableProvider.SubProviderEntry(s, ctx) },
                event.lookupProvider
            )
        }

        translationEntries.forEach { (locale, entries) ->
            event.createProvider {
                DynamicLanguageProvider(it, modId, locale, entries)
            }
        }

        event.createProvider { output, provider -> DynamicRecipeProvider(output, provider, recipeBuilders) }

        event.createProvider {
            DatapackBuiltinEntriesProvider(
                it,
                event.lookupProvider,
                RegistrySetBuilder().apply {
                    builtInProviders.groupBy { u -> u.first }
                        .mapValues { u -> u.value.map { pair -> pair.second } }
                        .forEach { (k, s) -> add(k) { ctx -> s.forEach { u -> u(ctx) } } }
                },
                mutableSetOf(modId)
            )
        }



        featureTagProviders.forEach { (registry, u) ->
            event.createProvider {
                DynamicTagProvider(
                    registry as Registry<Any>,
                    registry.key() as ResourceKey<out Registry<Any>>,
                    u as List<() -> Pair<ResourceKey<Any>, TagKey<Any>>>,
                    arrayListOf(),
                    arrayListOf(),
                    arrayListOf(),
                    it,
                    event.lookupProvider,
                    modId
                )
            }
        }
        optionalFeatureTagProviders.forEach { (registry, u) ->
            event.createProvider {
                DynamicTagProvider(
                    registry as Registry<Any>,
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    u as List<() -> Pair<ResourceLocation, TagKey<Any>>>,
                    arrayListOf(),
                    arrayListOf(),
                    it,
                    event.lookupProvider,
                    modId,
                )
            }
        }
        tagTagProviders.forEach { (registry, u) ->
            event.createProvider {
                DynamicTagProvider(
                    registry as Registry<Any>,
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    arrayListOf(),
                    u as List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                    arrayListOf(),
                    it,
                    event.lookupProvider,
                    modId
                )
            }
        }
        optionalTagTagProvider.forEach { (registry, u) ->
            event.createProvider {
                DynamicTagProvider(
                    registry as Registry<Any>,
                    registry.key() as ResourceKey<out Registry<Any>>,
                    arrayListOf(),
                    arrayListOf(),
                    arrayListOf(),
                    u as List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                    it,
                    event.lookupProvider,
                    modId,
                )
            }
        }

        providers.forEach { event.createProvider(it) }
    }

    fun registerServer(event: GatherDataEvent.Server) {

    }

    @Scan
    companion object {

        @ObjectScanner(DataGenHelper::class)
        fun attachDataGenHelperToEvent(info: IModInfo, container: ModContainer, data: DataGenHelper) {
            if (info.modId != data.modId) return

            container.eventBus?.addListener(data::registerClient)
            container.eventBus?.addListener(data::registerServer)
        }
    }
}

