package io.kito.kore.common.registry

import net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.*
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * A utility class for registering custom [CreativeModeTab]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of creative tabs
 * with NeoForge, simplifying the process of adding new tabs to the game.
 *
 * @property id The mod ID or namespace for these creative tabs.
 */
open class CreativeModeTabRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [CreativeModeTab]s, tied to the given mod ID.
     */
    private val register: DeferredRegister<CreativeModeTab> = DeferredRegister.create(CREATIVE_MODE_TAB, id)

    /**
     * Infix function to define a new [CreativeModeTab] with a builder for its properties.
     * This is the first step in a chain to addEntry a creative tab.
     *
     * @param name The name of the creative tab (e.g., "my_tab").
     * @param builder A lambda that takes a [CreativeModeTab.Builder] and the tab name, and applies properties to it.
     * @return A [DeferredHolder] for the registered [CreativeModeTab].
     */
    infix fun String.where(builder: Builder.(String) -> Unit): DeferredHolder<CreativeModeTab, CreativeModeTab> =
        register.register(this, builder().apply { builder(this, this@where) } ::build)

    /**
     * Extension function for [CreativeModeTab.Builder] to define how items are displayed in the tab.
     * @param builder A lambda that takes an [Output] and [ItemDisplayParameters] to define item display logic.
     * @return This [CreativeModeTab.Builder] for fluent chaining.
     */
    fun Builder.display(builder: Output.(ItemDisplayParameters) -> Unit) =
        also { displayItems { p, o -> builder(o, p) } }

    /**
     * Extension function for [CreativeModeTab.Output] to accept multiple [ItemStack]s.
     * @param stacks A vararg of lambdas, each supplying an [ItemStack] to be added to the tab.
     */
    fun Output.stacks(vararg stacks: () -> ItemStack) = acceptAll(stacks.asList().map { it() })

    /**
     * Extension function for [CreativeModeTab.Output] to accept multiple [ItemLike]s.
     * @param items A vararg of lambdas, each supplying an [ItemLike] to be added to the tab.
     */
    fun Output.items(vararg items: () -> ItemLike) =
        acceptAll(items.asList().map { it().asItem().defaultInstance })

    /**
     * Extension function for [CreativeModeTab.Output] to accept items from multiple [FluidTypeRegister.FluidTypeTemplate]s.
     * This automatically collects all bucket items from the templates and adds them to the tab.
     * @param templates A vararg of [FluidTypeRegister.FluidTypeTemplate]s.
     */
    fun <T> Output.templates(vararg templates: FluidTypeRegister.FluidTypeTemplate<T, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.flowingFluid.bucketItem[i]!! } } }.toTypedArray())

    /**
     * Extension function for [CreativeModeTab.Output] to accept items from multiple [FlowingFluidRegister.FlowingFluidTemplate]s.
     * This automatically collects all bucket items from the templates and adds them to the tab.
     * @param templates A vararg of [FlowingFluidRegister.FlowingFluidTemplate]s.
     */
    fun <T> Output.templates(vararg templates: FlowingFluidRegister.FlowingFluidTemplate<T, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.bucketItem[i]!! } } }.toTypedArray())

    /**
     * Extension function for [CreativeModeTab.Output] to accept items from multiple [BlockRegister.BlockTemplate]s.
     * This automatically collects all item forms of blocks from the templates and adds them to the tab.
     * @param templates A vararg of [BlockRegister.BlockTemplate]s.
     */
    fun <T> Output.templates(vararg templates: BlockRegister.BlockTemplate<T, *, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.item[i]!! } } }.toTypedArray())

    /**
     * Extension function for [CreativeModeTab.Output] to accept items from multiple [EntityTypeRegister.EntityTypeTemplate]s.
     * This automatically collects all spawn eggs from the templates and adds them to the tab.
     * @param templates A vararg of [EntityTypeRegister.EntityTypeTemplate]s.
     */
    fun <T> Output.templates(vararg templates: EntityTypeRegister.EntityTypeTemplate<T, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.egg[i]!! } } }.toTypedArray())

    /**
     * Extension function for [CreativeModeTab.Output] to accept items from multiple [RegistryTemplate]s.
     * This automatically collects all registered items from the templates and adds them to the tab.
     * @param templates A vararg of [RegistryTemplate]s.
     */
    fun <T> Output.templates(vararg templates: RegistryTemplate<T, out Item>) =
        items(*templates.flatMap { it.registereds }.toTypedArray())

    /**
     * Registers the [DeferredRegister] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined creative tabs.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register.register(bus)
    }
}

