package io.kito.kore.common.datagen

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.loc
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.core.registries.BuiltInRegistries.ITEM
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforgespi.language.IModInfo
import net.minecraft.world.item.CreativeModeTab.Builder as TabBuilder


typealias   ItemModelBuilder<T> =  ItemModelProvider.(ResourceLocation, T) -> Unit
typealias  BlockStateBuilder<T> = BlockStateProvider.(ResourceLocation, T) -> Unit
typealias TranslationBuilder = LanguageProvider.() -> Unit

abstract class DataGenHelper(private val modId: String) {

    private val  itemModelBuilders = arrayListOf<Pair<ResourceLocation, ItemModelBuilder<Item>>>()
    private val blockModelBuilders = arrayListOf<Pair<ResourceLocation, BlockStateBuilder<Block>>>()

    private val translationEntries = hashMapOf<String, ArrayList<TranslationBuilder>>()

    private val translationEntries2 = hashMapOf<String, ArrayList<TranslationBuilder>>()

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

    fun ItemBuilder<*>.defaultModel() { model { _, it -> basicItem(it) } }
    fun ItemBuilder<*>.blockModel() { model { loc, _ -> simpleBlockItem(loc) } }
    fun ItemBuilder<*>.spawnEggModel() { model { loc, _ -> spawnEggItem(loc) } }

    fun ItemBuilder<*>.named(vararg entries: Pair<String, String>)
        { entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } +=
            { add(ITEM[loc(modId, this@named.name)], it.second) } } }

    fun TabBuilder.named(name: String, vararg entries: Pair<String, String>)
    { loc(modId, name).toLanguageKey("itemGroup").let { key ->
        title(Component.translatable(key))
        entries.forEach { translationEntries.computeIfAbsent(it.first) { arrayListOf() } += { add(key, it.second) } }
    } }

    fun register(event: GatherDataEvent) {
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
    }

    interface LanguageSetter {
        operator fun set(locale: String, value: String)
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
