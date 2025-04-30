package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.loc
import io.kito.kore.util.neoforge.ResourceLocationExt.item
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.core.registries.BuiltInRegistries.ITEM
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.Block
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.conditions.ICondition
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforgespi.language.IModInfo
import net.minecraft.world.item.CreativeModeTab.Builder as TabBuilder


typealias   ItemModelBuilder<T> =  ItemModelProvider.(ResourceLocation, T) -> Unit
typealias  BlockStateBuilder<T> = BlockStateProvider.(ResourceLocation, T) -> Unit
typealias      RecipeBuilder<T> = (HolderLookup.Provider) -> Recipe<T>
typealias TranslationBuilder = LanguageProvider.() -> Unit

abstract class DataGenHelper(private val modId: String) {

    private val  itemModelBuilders = arrayListOf<Pair<ResourceLocation, ItemModelBuilder<Item>>>()
    private val blockModelBuilders = arrayListOf<Pair<ResourceLocation, BlockStateBuilder<Block>>>()
    private val     recipeBuilders = arrayListOf<RecipeOutput.(HolderLookup.Provider) -> Unit>()

    private val translationEntries = hashMapOf<String, ArrayList<TranslationBuilder>>()

    val providers = arrayListOf<Pair<Dist, (PackOutput) -> DataProvider>>()

    internal val blocks = arrayListOf<() -> Unit>()

    @Suppress(UNCHECKED_CAST)
    fun <T : Block> BlockBuilder<T>.state(builder: BlockStateBuilder<T>)
        { blockModelBuilders += loc(modId, blockName) to (builder as BlockStateBuilder<Block>) }

    fun BlockBuilder<*>.defaultState() { state { _, it -> simpleBlock(it) } }
    fun BlockBuilder<*>.defaultStateAndItemModel() { state { _, it -> simpleBlockWithItem(it, cubeAll(it)) } }

    fun BlockBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(BLOCK[loc(modId, this@named.blockName)], it.second) } } }


    @Suppress(UNCHECKED_CAST)
    fun <T : Item> ItemBuilder<T>.model(builder: ItemModelBuilder<T>)
        { itemModelBuilders += loc(modId, name)  to (builder as ItemModelBuilder<Item>) }

    fun ItemBuilder<*>.defaultModel() { model { loc, _ -> basicItem(loc) } }
    fun ItemBuilder<*>.blockModel() { model { loc, _ -> simpleBlockItem(loc) } }
    fun ItemBuilder<*>.spawnEggModel() { model { loc, _ -> spawnEggItem(loc) } }

    fun ItemBuilder<*>.bucketModel() { model { loc, _ ->
        withExistingParent("$loc", mcLoc("item/generated"))
            .texture("layer0", mcLoc("bucket").item)
            .texture("layer1", loc.item)
    } }

    fun ItemBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(ITEM[loc(modId, this@named.name)], it.second) } } }


    fun TabBuilder.named(name: String, vararg entries: Pair<String, String>)
    { loc(modId, name).toLanguageKey("itemGroup").let { key ->
        title(Component.translatable(key))
        entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } += { add(key, it.second) } }
    } }

    fun <T : RecipeInput> recipe(loc: ResourceLocation, builder: RecipeBuilder<T>) {
        recipeBuilders += { accept(loc, builder(it), null) }
    }

    fun <T : RecipeInput> recipe(loc: ResourceLocation, advancement: AdvancementHolder, builder: RecipeBuilder<T>) {
        recipeBuilders += { accept(loc, builder(it), advancement) }
    }

    fun <T : RecipeInput> recipe(loc: ResourceLocation,
                                 advancement: AdvancementHolder,
                                 vararg condidions: ICondition,
                                 builder: RecipeBuilder<T>)
    { recipeBuilders += { accept(loc, builder(it), advancement, *condidions) } }

    fun register(event: GatherDataEvent) {
        blocks.forEach { it() }

        val generator = event.generator

        generator.addProvider(
            event.includeClient(),
            DynamicBlockStateProvider(generator.packOutput, modId, event.existingFileHelper,
                blockModelBuilders.map { { p -> it.second(p, it.first, BLOCK[it.first]) } })
        )
        generator.addProvider(
            event.includeClient(),
            DynamicItemModelProvider(generator.packOutput, modId, event.existingFileHelper,
                                     itemModelBuilders.map { { p -> it.second(p, it.first, ITEM[it.first]) } })
        )

        translationEntries.forEach { (locale, entries) ->
            generator.addProvider(event.includeClient(),
                                  DynamicLanguageProvider(generator.packOutput, modId, locale, entries)
            )
        }

        generator.addProvider(event.includeClient(),
            DynamicRecipeProvider(generator.packOutput, event.lookupProvider, recipeBuilders))

        providers.forEach { (d, c) ->
            generator.addProvider(when (d) {
                Dist.CLIENT -> event.includeClient()
                Dist.DEDICATED_SERVER -> event.includeServer()
            }, c(generator.packOutput))
        }
    }

    @Scan
    companion object {

        @ObjectScanner(DataGenHelper::class)
        fun attachDataGenHelperToEvent(info: IModInfo, container: ModContainer, data: DataGenHelper) {
            if (info.modId != data.modId) return

            container.eventBus?.addListener(data::register)
        }
    }
}
