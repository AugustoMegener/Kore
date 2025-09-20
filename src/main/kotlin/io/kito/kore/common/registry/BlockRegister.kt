package io.kito.kore.common.registry

import io.kito.kore.common.capabilities.BlockCapRegister
import io.kito.kore.common.capabilities.BlockCapRegister.BlockCapRegistry
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.blockProp
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible
import net.minecraft.world.item.Item.Properties as ItemProp
import io.kito.kore.common.registry.BlockEntityTypeRegister.BETBuilder
import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGoup
import io.kito.kore.common.template.Template
import io.kito.kore.util.Indexable
import io.kito.kore.util.minecraft.BlockProp
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapability

/**
 * A utility class for registering custom [Block]s, [BlockItem]s, and [BlockEntityType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of these components
 * with NeoForge, simplifying the process of adding new blocks to the game.
 *
 * @property id The mod ID or namespace for these registrations.
 */
open class BlockRegister(final override val id: String) : AutoRegister {

    /**
     * An [ItemRegister] instance used for registering [BlockItem]s associated with blocks.
     */
    private val itemRegister =            ItemRegister(id)
    /**
     * A [BlockEntityTypeRegister] instance used for registering [BlockEntityType]s associated with blocks.
     */
    private val   beRegister = BlockEntityTypeRegister(id)
    /**
     * A [DeferredRegister] specifically for [Block]s, tied to the given mod ID.
     */
    private val     register = DeferredRegister.createBlocks(id)

    /**
     * Infix function to define a new [Block] with a supplier for creating [Block] instances.
     * This is the first step in a chain to addEntry a block.
     *
     * @param T The type of the [Block] to be registered.
     * @param supplier A lambda that supplies a new instance of the [Block] given [BlockProp].
     * @return A [BlockBuilder] to continue the registration process.
     */
    infix fun <T : Block> String.of(supplier: (BlockProp) -> T) = BlockBuilder(this, supplier)

    /**
     * Infix function to define a new [Block] with a supplier for creating [Block] instances, followed by a `where` clause.
     * This is an alternative syntax for starting the block registration chain.
     *
     * @param T The type of the [Block] to be registered.
     * @param supplier A lambda that supplies a new instance of the [Block] given [BlockProp].
     * @return A [BlockBuilder] to continue the registration process.
     */
    infix fun <T : Block> String.where(supplier: (BlockProp) -> T) = BlockBuilder(this, supplier) where {}

    /**
     * Registers all deferred registers ([Block], [Item], [BlockEntityType]) with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined blocks and their associated components.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register    .register(bus)
        itemRegister.register(bus)
        beRegister  .register(bus)
    }

    /**
     * Extension property to retrieve the [BlockItem] associated with a [Block] property.
     * This is useful for accessing the item form of a registered block.
     *
     * @receiver The [KProperty0] representing the [Block] property.
     * @return The [DeferredItem] for the [BlockItem] associated with the block.
     * @throws IllegalStateException if the property does not have a delegation from [BlockRegistry].
     */
    val KProperty0<Block>.blockItem get() =
        (also { isAccessible = true }.getDelegate() as? BlockRegistry<*>)?.itemRegistry
        ?: throw IllegalStateException("Property does not have a delegation from type ${BlockRegistry::class}")

    /**
     * Extension function to retrieve the [BlockItem] associated with a [Block] property, cast to a specific type.
     *
     * @param I The expected type of the [BlockItem].
     * @receiver The [KProperty0] representing the [Block] property.
     * @return The [BlockItem] cast to type [I].
     * @throws IllegalStateException if the property does not provide an item of the specified type.
     */
    @Suppress(UNCHECKED_CAST)
    fun <I : BlockItem> KProperty0<Block>.blockItem() = blockItem as? I
        ?: throw IllegalStateException("Property does not provide an item from the specified type")

    /**
     * Extension property to retrieve the [BlockEntity] associated with a [Block] property.
     * This is useful for accessing the block entity associated with a registered block.
     *
     * @receiver The [KProperty0] representing the [Block] property.
     * @return The [DeferredBET] for the [BlockEntity] associated with the block.
     * @throws IllegalStateException if the property does not have a delegation from [BlockRegistry].
     */
    val KProperty0<Block>.blockEntity get() =
        (also { isAccessible = true }.getDelegate() as? BlockRegistry<*>)?.beRegistry
        ?: throw IllegalStateException("Property does not have a delegation from type ${BlockRegistry::class}")

    /**
     * Extension function to retrieve the [BlockEntity] associated with a [Block] property, cast to a specific type.
     *
     * @param B The expected type of the [BlockEntity].
     * @receiver The [KProperty0] representing the [Block] property.
     * @return The [BlockEntity] cast to type [B].
     * @throws IllegalStateException if the property does not provide a block entity of the specified type.
     */
    @Suppress(UNCHECKED_CAST)
    fun <B : BlockEntity> KProperty0<Block>.blockEntity() = blockEntity as? DeferredBET<B>
        ?: throw IllegalStateException("Property does not provide a BE from the specified type")


    /**
     * Inner class to facilitate the building and registration of [Block]s.
     *
     * @param B The type of the [Block] that this builder handles.
     * @property blockName The name of the block.
     * @property supplier A lambda that supplies a new instance of the [Block] given [BlockProp].
     */
    inner class BlockBuilder<B : Block>(val blockName: String, private val supplier: (BlockProp) -> B)
    {

        val blockId = loc(id, blockName)

        private var blockItemSupplier : (B, ItemProp) -> BlockItem = ::BlockItem
        private var blockItemBuilder  : ItemRegister.ItemBuilder<out BlockItem>.() -> Unit = {}

        /**
         * Lazily initialized supplier for the [BlockItem] associated with this block.
         */
        val blockItem by lazy { { b: () -> B -> itemRegister.ItemBuilder(blockName) { blockItemSupplier(b(), it) } } }

        private var blockEntitySupplier : BlockEntitySupplier?              =   null
        private var blockEntityBuilder  : BETBuilder<BlockEntity>.() -> Unit = {}

        private var props: (BlockProp) -> BlockProp = { it }

        private val blockCaps = BlockCaps()

        /**
         * Configures the properties of the block.
         * @param block A lambda that takes a [BlockProp] instance and applies properties to it.
         * @return This [BlockBuilder] for fluent chaining.
         */
        fun props(block: BlockProp.() -> BlockProp) {
            val previous = props
            props = { previous(it).block() }
        }


        /**
         * Sets the default [BlockItem] builder for this block.
         * @param builder A lambda that takes an [ItemBuilder] for [BlockItem] and applies configurations.
         */
        @Suppress(UNCHECKED_CAST)
        fun defaultItem(builder: ItemBuilder<BlockItem>.() -> Unit)
            { blockItemBuilder = builder as ItemBuilder<out BlockItem>.() -> Unit }

        /**
         * Sets a custom [BlockItem] supplier and builder for this block.
         * @param T The type of the custom [BlockItem].
         * @param item A lambda that supplies a new instance of the custom [BlockItem] given the block and [ItemProp].
         * @param builder A lambda that takes an [ItemBuilder] for the custom [BlockItem] and applies configurations.
         */
        @Suppress(UNCHECKED_CAST)
        fun  <T : BlockItem> item(item: (B, ItemProp) -> BlockItem, builder:  ItemBuilder<T>.() -> Unit)
            { blockItemBuilder = builder as ItemBuilder<out BlockItem>.() -> Unit; blockItemSupplier = item }


        /**
         * Sets the [BlockEntitySupplier] for this block.
         * @param supplier A lambda that supplies a new instance of the [BlockEntity].
         */
        fun blockEntity(supplier: BlockEntitySupplier) { blockEntitySupplier = supplier }

        /**
         * Sets a custom [BlockEntity] supplier and builder for this block.
         * @param T The type of the custom [BlockEntity].
         * @param supplier A lambda that supplies a new instance of the custom [BlockEntity] given [BlockPos] and [BlockState].
         * @param builder A lambda that takes a [BETBuilder] for the custom [BlockEntity] and applies configurations.
         */
        fun <T : BlockEntity> blockEntity(supplier: (BlockPos, BlockState) -> T, builder: BETBuilder<T>.() -> Unit) {
            blockEntitySupplier = supplier
            blockEntityBuilder = builder as BETBuilder<BlockEntity>.() -> Unit
        }

        /**
         * Configures capabilities for this block.
         * @param adder A lambda that takes a [BlockCaps] instance and adds capabilities to it.
         * @return This [BlockBuilder] for fluent chaining.
         */
        fun caps(adder: BlockCaps.() -> Unit) = also { blockCaps.apply(adder) }

        /**
         * Inner class for defining and collecting block capabilities.
         */
        inner class BlockCaps {
            /**
             * A list of [BlockCapRegistry] instances, each representing a capability to be registered.
             */
            val registries = arrayListOf<BlockCapRegistry<*, *, *>>()

            /**
             * Operator function to add a capability with a context object.
             * @param O The type of the capability object.
             * @param C The type of the context object.
             * @param getter A lambda that takes [Level], [BlockPos], [BlockState], optional [BlockEntity], and context object, and returns the capability object.
             */
            operator fun <O, C> BlockCapability<O, C>.invoke(getter: Level.(BlockPos, BlockState, BlockEntity?, C?) -> O)
            { registries += BlockCapRegistry(this, getter) }

            /**
             * Operator function to add a capability without a context object (Void?).
             * @param O The type of the capability object.
             * @param getter A lambda that takes [Level], [BlockPos], [BlockState], and optional [BlockEntity], and returns the capability object.
             */
            operator fun <O   > BlockCapability<O, Void?>.invoke(getter: Level.(BlockPos, BlockState, BlockEntity?) -> O)
            { registries += BlockCapRegistry(this) { lvl, pos, state, be, _ -> getter(lvl, pos, state, be) } }
        }

        /**
         * Finalizes the block registration process.
         * This method registers the block, its item, and its block entity (if any) with NeoForge.
         *
         * @param builder A lambda that takes this [BlockBuilder] and applies additional configurations.
         * @return A [BlockRegistry] instance containing the registered components.
         */
        infix fun where(builder: BlockBuilder<B>.() -> Unit) : BlockRegistry<B> {
            apply(builder)

            val reg = register.register(blockName) { -> supplier(props(blockProp())) }

            BlockCapRegister.blockCaps += { reg.value() } to blockCaps.registries

            return BlockRegistry(
                reg, blockItem { reg.value() } where blockItemBuilder,
                blockEntitySupplier
                    ?.let { be -> with(beRegister) { (blockName of be).apply(blockEntityBuilder) on { reg.value() } } })
        }
    }

    /**
     * Creates a [BlockTemplate] for blocks without a specific item type or block entity type.
     * @param T The type of the index for the template.
     * @param B The type of the [Block].
     * @param builder A lambda that takes an index of type [T] and returns a [BlockRegistry].
     * @return A new [BlockTemplate] instance.
     */
    fun <T, B : Block> blockTemplate(registry: EarlyRegistry<T>, builder: (T) -> BlockRegistry<B>) =
        BlockTemplate<T, B, BlockItem, Nothing>(registry, builder)

    /**
     * Creates a [BlockTemplate] for blocks with a specific item type but no block entity type.
     * @param T The type of the index for the template.
     * @param B The type of the [Block].
     * @param I The type of the [BlockItem].
     * @param builder A lambda that takes an index of type [T] and returns a [BlockRegistry].
     * @return A new [BlockTemplate] instance.
     */
    fun <T, B : Block, I : BlockItem> blockTemplateWithItem(registry: EarlyRegistry<T>, builder: (T) -> BlockRegistry<B>) =
        BlockTemplate<T, B, I, Nothing>(registry, builder)

    /**
     * Creates a [BlockTemplate] for blocks with a specific block entity type but no specific item type.
     * @param T The type of the index for the template.
     * @param B The type of the [Block].
     * @param E The type of the [BlockEntity].
     * @param builder A lambda that takes an index of type [T] and returns a [BlockRegistry].
     * @return A new [BlockTemplate] instance.
     */
    fun <T, B : Block, E : BlockEntity> blockTemplateWithEntity(registry: EarlyRegistry<T>,
                                                                builder: (T) -> BlockRegistry<B>) =
        BlockTemplate<T, B, BlockItem, E>(registry, builder)

    /**
     * Creates a [BlockTemplate] for blocks with both a specific item type and a specific block entity type.
     * @param T The type of the index for the template.
     * @param B The type of the [Block].
     * @param I The type of the [BlockItem].
     * @param E The type of the [BlockEntity].
     * @param builder A lambda that takes an index of type [T] and returns a [BlockRegistry].
     * @return A new [BlockTemplate] instance.
     */
    fun <T, B : Block, I : BlockItem, E : BlockEntity> blockTemplateFull(registry: EarlyRegistry<T>,
                                                                         builder: (T) -> BlockRegistry<B>) =
        BlockTemplate<T, B, I, E>(registry, builder)

    /**
     * A template class for registering multiple blocks based on an index.
     * This allows for defining a common structure for a set of related blocks.
     *
     * @param T The type of the index used to identify individual blocks within the template.
     * @param B The base type of the [Block]s in this template.
     * @param I The type of the [BlockItem]s associated with the blocks in this template.
     * @param E The type of the [BlockEntity]s associated with the blocks in this template.
     */
    class BlockTemplate<T, B : Block, I : BlockItem, E : BlockEntity>(override val registry: EarlyRegistry<T>,
                                                                      val builder: (T) -> BlockRegistry<B>)
        : Template<T, B>
    {
        // This map will store the *actually* registered items, after the call to register()
        private val registeredEntries = hashMapOf<T, BlockRegistry<B>>()

        /**
         * An [Indexable] property to access the [BlockItem]s by their index.
         */
        val item   = object : Indexable<T, I?> { override fun get(idx: T) = registeredEntries[idx]?.itemRegistry?.get() as I? }
        /**
         * An [Indexable] property to access the [BlockEntity]s by their index.
         */
        val entity = object : Indexable<T, E?> { override fun get(idx: T) = registeredEntries[idx]?.beRegistry  ?.get() as E? }

        /**
         * Retrieves a [Block] by its index.
         * @param idx The index of the block.
         * @return The [Block] instance, or `null` if not found.
         */
        override fun get(idx: T): B? = registeredEntries[idx]?.blockRegistry?.get()

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
     * A data class representing the registered components of a block.
     * This class holds references to the [DeferredBlock], [DeferredItem], and optional [DeferredBET].
     *
     * @param T The type of the [Block] that is registered.
     * @property blockRegistry The [DeferredBlock] for the registered block.
     * @property itemRegistry The [DeferredItem] for the [BlockItem] associated with the block.
     * @property beRegistry The optional [DeferredBET] for the [BlockEntity] associated with the block.
     */
    inner class BlockRegistry<T : Block>(val blockRegistry : DeferredBlock<      T      >,
                                         val  itemRegistry : DeferredItem <out BlockItem>,
                                         val    beRegistry : DeferredBET<BlockEntity>?)
    {
        /**
         * Delegate function for property access, returning the registered [Block] instance.
         */
        operator fun getValue(obj: Any,     property: KProperty<*>) : T = blockRegistry.value()
        /**
         * Delegate function for property access, returning the registered [Block] instance.
         */
        operator fun getValue(obj: Nothing, property: KProperty<*>) : T = blockRegistry.value()

        /**
         * The [ResourceLocation] key of the registered block.
         */
        val key get() = blockRegistry.key
    }
}

