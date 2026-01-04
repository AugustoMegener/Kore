package io.kito.kore.common.registry

import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.common.registry.BlockRegister.BlockRegistry
import io.kito.kore.common.registry.FluidTypeRegister.FluidTypeRegistry
import io.kito.kore.common.registry.FluidTypeRegister.FluidTypeTemplate
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGoup
import io.kito.kore.common.template.Template
import io.kito.kore.util.Indexable
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.BlockProp
import io.kito.kore.util.minecraft.FlowingFluidProp
import io.kito.kore.util.minecraft.ItemProp
import net.minecraft.core.registries.BuiltInRegistries.FLUID
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Items.BUCKET
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.PushReaction
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

typealias DeferredFluid<T> = DeferredHolder<Fluid, T>

/**
 * A utility class for registering custom [FlowingFluid]s, their associated [LiquidBlock]s, and [BucketItem]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of these components
 * with NeoForge, simplifying the process of adding new fluids to the game.
 *
 * @property id The mod ID or namespace for these registrations.
 */
open class FlowingFluidRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [Fluid]s, tied to the given mod ID.
     */
    private val register = DeferredRegister.create(FLUID, id)
    /**
     * A [BlockRegister] instance used for registering [LiquidBlock]s associated with fluids.
     */
    private val blockRegister = BlockRegister(id)
    /**
     * An [ItemRegister] instance used for registering [BucketItem]s associated with fluids.
     */
    private val itemRegister = ItemRegister(id)

    /**
     * Infix function to define a new flowing fluid, starting from a [FluidType].
     * This is the first step in a chain to addEntry a flowing fluid.
     *
     * @param name The name of the fluid (e.g., "my_fluid").
     * @param type A lambda that supplies the [FluidType] for this fluid.
     * @return A [FlowingFluidBuilder] to continue the registration process.
     */
    infix fun String.from(type: () -> FluidType) = FlowingFluidBuilder(this, type)

    /**
     * Inner class to facilitate the building and registration of [FlowingFluid]s.
     *
     * @property name The name of the fluid.
     * @property type A lambda that supplies the [FluidType] for this fluid.
     */
    inner class FlowingFluidBuilder(val name: String, val type: () -> FluidType) {
        /**
         * Properties supplier for [BaseFlowingFluid.Properties].
         * This lambda configures the properties for both source and flowing fluid blocks.
         */
        private val prop = { s: DeferredFluid<Source>, f: DeferredFluid<Flowing> ->
            BaseFlowingFluid.Properties(type, s, f).apply {
                if (makeLiquidBlock) block  { liquidBlock.blockRegistry.get() }
                if (makeBucketItem)  bucket { bucketItem.get() }
            }.apply(propConfig)
        }

        private var propConfig: FlowingFluidProp.() -> Unit = {}

        private var  sourcePropConfig: FlowingFluidProp.() -> Unit = {}
        private var flowingPropConfig: FlowingFluidProp.() -> Unit = {}

        /**
         * Flag to control whether a [LiquidBlock] should be created for this fluid.
         */
        var makeLiquidBlock = true

        /**
         * Lazily initialized [BlockRegistry] for the [LiquidBlock] associated with this fluid.
         */
        val liquidBlock by lazy {
            with(blockRegister) { name of { liquidBlockSupplier(sourceRegistry.get(), it) } where liquidBlockBuilder }
        }

        private var liquidBlockSupplier: (FlowingFluid, BlockProp) -> LiquidBlock = ::LiquidBlock
        private var liquidBlockBuilder: BlockBuilder<out LiquidBlock>.() -> Unit = {
            props {
                replaceable()
                //noCollission()
                strength(100.0F)
                pushReaction(PushReaction.DESTROY)
                noLootTable()
                liquid()
                sound(SoundType.EMPTY)
            }
        }

        /**
         * Flag to control whether a [BucketItem] should be created for this fluid.
         */
        var makeBucketItem = true

        var bucketItemName = "${name}_bucket"


        val bucketItem by lazy {
            with(itemRegister) { bucketItemName of { bucketItemSupplier(sourceRegistry.get(), it) } where bucketItemBuilder }
        }

        private var bucketItemSupplier: (FlowingFluid, ItemProp) -> BucketItem = ::BucketItem
        private var bucketItemBuilder: ItemBuilder<out BucketItem>.() -> Unit = {
            props {
                craftRemainder(BUCKET)
                stacksTo(1)
            }
        }

        /**
         * Lazily initialized [DeferredHolder] for the flowing fluid.
         */
        private val flowingRegistry: DeferredHolder<Fluid, Flowing> by lazy {
            register.register("flowing_$name") { -> Flowing(prop(sourceRegistry, flowingRegistry).apply(flowingPropConfig)) }
        }

        /**
         * Lazily initialized [DeferredHolder] for the source fluid.
         */
        private val sourceRegistry: DeferredHolder<Fluid, Source> by lazy {
            register.register("source_$name") { -> Source(prop(sourceRegistry, flowingRegistry).apply(sourcePropConfig)) }
        }

        /**
         * Configures the [LiquidBlock] builder for this fluid.
         * @param builder A lambda that takes a [BlockBuilder] for [LiquidBlock] and applies configurations.
         */
        fun liquidBlock(builder: BlockBuilder<out LiquidBlock>.() -> Unit) { liquidBlockBuilder = builder }

        /**
         * Sets a custom [LiquidBlock] supplier and builder for this fluid.
         * @param T The type of the custom [LiquidBlock].
         * @param supplier A lambda that supplies a new instance of the custom [LiquidBlock] given a [FlowingFluid] and [BlockProp].
         * @param builder A lambda that takes a [BlockBuilder] for the custom [LiquidBlock] and applies configurations.
         */
        @Suppress(UNCHECKED_CAST)
        fun <T : LiquidBlock> liquidBlock(supplier: (FlowingFluid, BlockProp) -> T,
                                          builder: BlockBuilder<out T>.() -> Unit)
        { liquidBlockSupplier = supplier
          liquidBlockBuilder = builder as BlockBuilder<out LiquidBlock>.() -> Unit }

        /**
         * Configures the [BucketItem] builder for this fluid.
         * @param builder A lambda that takes an [ItemBuilder] for [BucketItem] and applies configurations.
         */
        @Suppress(UNCHECKED_CAST)
        fun bucketItem(builder: ItemBuilder<BucketItem>.() -> Unit) {
            bucketItemBuilder = builder as ItemBuilder<out BucketItem>.() -> Unit
        }

        /**
         * Sets a custom [BucketItem] supplier and builder for this fluid.
         * @param T The type of the custom [BucketItem].
         * @param supplier A lambda that supplies a new instance of the custom [BucketItem] given a [FlowingFluid] and [ItemProp].
         * @param builder A lambda that takes an [ItemBuilder] for the custom [BucketItem] and applies configurations.
         */
        @Suppress(UNCHECKED_CAST)
        fun <T : BucketItem> bucketItem(supplier: (FlowingFluid, ItemProp) -> T,
                                        builder : ItemBuilder<T>.() -> Unit)
        { bucketItemSupplier = supplier
          bucketItemBuilder = builder as ItemBuilder<out BucketItem>.() -> Unit }

        /**
         * Configures the common properties for both source and flowing fluid blocks.
         * @param action A lambda that takes a [FlowingFluidProp] instance and applies properties to it.
         */
        fun props(action: FlowingFluidProp.() -> Unit) { propConfig = action }

        /**
         * Configures the properties specifically for the source fluid block.
         * @param action A lambda that takes a [FlowingFluidProp] instance and applies properties to it.
         */
        fun  sourceProps(action: FlowingFluidProp.() -> Unit) { sourcePropConfig = action }
        /**
         * Configures the properties specifically for the flowing fluid block.
         * @param action A lambda that takes a [FlowingFluidProp] instance and applies properties to it.
         */
        fun flowingProps(action: FlowingFluidProp.() -> Unit) { flowingPropConfig = action }

        /**
         * Finalizes the flowing fluid registration process.
         * This method registers the source fluid, flowing fluid, liquid block, and bucket item with NeoForge.
         *
         * @param action A lambda that takes this [FlowingFluidBuilder] and applies additional configurations.
         * @return A [FlowingFluidRegistry] instance containing the registered components.
         */
        infix fun where(action: FlowingFluidBuilder.() -> Unit): FlowingFluidRegistry {
            apply(action)

            return FlowingFluidRegistry(
                sourceRegistry, flowingRegistry,
                if (makeLiquidBlock) liquidBlock else null,
                if (makeBucketItem) bucketItem else null
            )
        }
    }

    /**
     * A data class representing the registered components of a flowing fluid.
     * This class holds references to the source fluid, flowing fluid, optional liquid block, and optional bucket item.
     *
     * @property source The [DeferredHolder] for the source fluid.
     * @property flowing The [DeferredHolder] for the flowing fluid.
     * @property liquidBlock The optional [BlockRegistry] for the [LiquidBlock] associated with the fluid.
     * @property bucketItem The optional [DeferredItem] for the [BucketItem] associated with the fluid.
     */
    class FlowingFluidRegistry(val source: DeferredHolder<Fluid, Source>,
                               val flowing: DeferredHolder<Fluid, Flowing>,
                               val liquidBlock: BlockRegistry<out LiquidBlock>?,
                               val bucketItem: DeferredItem<out BucketItem>?)


    /**
     * A template class for registering multiple flowing fluids based on an index.
     * This allows for defining a common structure for a set of related fluids.
     *
     * @param T The type of the index used to identify individual fluids within the template.
     * @param B The base type of the [LiquidBlock]s in this template.
     * @param I The type of the [BucketItem]s associated with the fluids in this template.
     */
    class FlowingFluidTemplate<T, B: LiquidBlock, I: BucketItem>(override val registry: EarlyRegistry<T>,
                                                                 val builder: (T) -> FlowingFluidRegistry)
        : Template<T, FlowingFluidRegistry>
    {
        // This map will store the *actually* registered fluid registries, after the call to register()
        val registeredEntries = hashMapOf<T, FlowingFluidRegistry>()


        /**
         * An [Indexable] property to access the source [FlowingFluid]s by their index.
         */
        val source = object : Indexable<T, Source?> { override fun get(idx: T) = registeredEntries[idx]?.source?.get() }
        /**
         * An [Indexable] property to access the flowing [FlowingFluid]s by their index.
         */
        val flowing = object : Indexable<T, Flowing?> { override fun get(idx: T) = registeredEntries[idx]?.flowing?.get() }

        /**
         * An [Indexable] property to access the [BucketItem]s by their index.
         */
        val bucketItem   = object : Indexable<T, I?> {
            override fun get(idx: T) = registeredEntries[idx]?.bucketItem?.get() as I?
        }
        /**
         * An [Indexable] property to access the [LiquidBlock]s by their index.
         */
        val liquidBlock = object : Indexable<T, BlockRegistry<B>?> {
            override fun get(idx: T) = registeredEntries[idx]?.liquidBlock as BlockRegistry<B>?
        }

        /**
         * Retrieves a [FlowingFluidRegistry] by its index.
         * @param idx The index of the fluid.
         * @return The [FlowingFluidRegistry] instance, or `null` if not found.
         */
        override fun get(idx: T): FlowingFluidRegistry? = registeredEntries[idx]

        override val indexesIds = arrayListOf<ResourceLocation>()
        val groups = arrayListOf<EarlyRegistryGoup>()

        override fun putAllIndexes() { indexesIds += registry.idxs }

        override fun putIndex(id: ResourceLocation) { indexesIds += id }

        override fun putIndex(group: EarlyRegistryGoup) { groups += group }

        override fun register() {
            indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
            indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

            indexes.forEach { registeredEntries[it] = builder(it) }
        }
    }


    /**
     * Creates a [FlowingFluidTemplate] for fluids without a specific liquid block or bucket item type.
     * @param T The type of the index for the template.
     * @param builder A lambda that takes an index of type [T] and returns a [FlowingFluidRegistry].
     * @return A new [FlowingFluidTemplate] instance.
     */
    fun <T> flowingFluidTemplate(registry: EarlyRegistry<T>, builder: (T) -> FlowingFluidRegistry) =
        FlowingFluidTemplate<T, LiquidBlock, BucketItem>(registry, builder)

    /**
     * Creates a [FlowingFluidTemplate] for fluids with a specific liquid block type.
     * @param T The type of the index for the template.
     * @param B The type of the [LiquidBlock].
     * @param builder A lambda that takes an index of type [T] and returns a [FlowingFluidRegistry].
     * @return A new [FlowingFluidTemplate] instance.
     */
    fun <T, B: LiquidBlock> flowingFluidTemplateWithLiquidBlock(registry: EarlyRegistry<T>, builder: (T) -> FlowingFluidRegistry) =
        FlowingFluidTemplate<T, B, BucketItem>(registry, builder)

    /**
     * Creates a [FlowingFluidTemplate] for fluids with a specific bucket item type.
     * @param T The type of the index for the template.
     * @param I The type of the [BucketItem].
     * @param builder A lambda that takes an index of type [T] and returns a [FlowingFluidRegistry].
     * @return A new [FlowingFluidTemplate] instance.
     */
    fun <T, I: BucketItem> flowingFluidTemplateWithBucketItem(registry: EarlyRegistry<T>, builder: (T) -> FlowingFluidRegistry) =
        FlowingFluidTemplate<T, LiquidBlock, I>(registry, builder)

    /**
     * Creates a [FlowingFluidTemplate] for fluids with both a specific liquid block type and a specific bucket item type.
     * @param T The type of the index for the template.
     * @param B The type of the [LiquidBlock].
     * @param I The type of the [BucketItem].
     * @param builder A lambda that takes an index of type [T] and returns a [FlowingFluidRegistry].
     * @return A new [FlowingFluidTemplate] instance.
     */
    fun <T, B: LiquidBlock, I: BucketItem> flowingFluidTemplateFull(registry: EarlyRegistry<T>, builder: (T) -> FlowingFluidRegistry) =
        FlowingFluidTemplate<T, B, I>(registry, builder)

    /**
     * Registers the [DeferredRegister]s with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined fluids and their associated components.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register.register(bus)
        blockRegister.register(bus)
        itemRegister.register(bus)
    }
}

