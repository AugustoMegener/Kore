package io.kito.kore.common.registry

import com.mojang.datafixers.types.Type
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.blockProp
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
import net.minecraft.world.level.block.state.BlockBehaviour.Properties as BlockProp
import io.kito.kore.common.registry.BlockEntityTypeRegister.BETBuilder
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

open class BlockRegister(final override val id: String) : AutoRegister {

    private val itemRegister =            ItemRegister(id)
    private val   beRegister = BlockEntityTypeRegister(id)
    private val     register = DeferredRegister.createBlocks(id)

    private var registryFinalized = false

    infix fun <T : Block> String.of(supplier: (BlockProp) -> T) = BlockBuilder(this, supplier)

    infix fun <T : Block> String.where(supplier: (BlockProp) -> T) = BlockBuilder(this, supplier) where {}

    override fun register(bus: IEventBus) {
        register    .register(bus)
        itemRegister.register(bus)
        beRegister  .register(bus)
    }

    val KProperty0<Block>.blockItem get() =
        (also { isAccessible = true }.getDelegate() as? BlockRegistry<*>)?.itemRegistry
        ?: throw IllegalStateException("Property does not have a delegation of type ${BlockRegistry::class}")

    @Suppress(UNCHECKED_CAST)
    fun <I : BlockItem> KProperty0<Block>.blockItem() = blockItem as? I
        ?: throw IllegalStateException("Property does not provide an item of the specified type")

    val KProperty0<Block>.blockEntity get() =
        (also { isAccessible = true }.getDelegate() as? BlockRegistry<*>)?.beRegistry
        ?: throw IllegalStateException("Property does not have a delegation of type ${BlockRegistry::class}")

    @Suppress(UNCHECKED_CAST)
    fun <B : BlockEntity> KProperty0<Block>.blockEntity() = blockEntity as? B
        ?: throw IllegalStateException("Property does not provide a BE of the specified type")


    inner class BlockBuilder<B : Block>(val blockName: String, private val supplier: (BlockProp) -> B)
    {

        private var blockItemSupplier : (B, ItemProp) -> BlockItem = ::BlockItem
        private var blockItemBuilder  : ItemRegister.ItemBuilder<out BlockItem>.() -> Unit = {}

        val blockItem by lazy { { b: () -> B -> itemRegister.ItemBuilder(blockName) { blockItemSupplier(b(), it) } } }

        private var blockEntitySupplier : BlockEntitySupplier?              =   null
        private var blockEntityBuilder  : BETBuilder<BlockEntity>.() -> Unit = {}

        private val blockProp = blockProp()

        fun props(block: BlockProp.() -> Unit) = blockProp.apply(block)


        @Suppress(UNCHECKED_CAST)
        fun defaultItem(builder: ItemBuilder<BlockItem>.() -> Unit)
            { blockItemBuilder = builder as ItemBuilder<out BlockItem>.() -> Unit }

        @Suppress(UNCHECKED_CAST)
        fun  <T : BlockItem> item(item: (B, ItemProp) -> BlockItem, builder:  ItemBuilder<T>.() -> Unit)
            { blockItemBuilder = builder as ItemBuilder<out BlockItem>.() -> Unit; blockItemSupplier = item }


        fun blockEntity(supplier: BlockEntitySupplier) { blockEntitySupplier = supplier }

        fun <T : BlockEntity> blockEntity(supplier: (BlockPos, BlockState) -> T, builder: BETBuilder<T>.() -> Unit) {
            blockEntitySupplier = supplier
            blockEntityBuilder = builder as BETBuilder<BlockEntity>.() -> Unit
        }

        infix fun where(builder: BlockBuilder<B>.() -> Unit) : BlockRegistry<B> {
            apply(builder)

            val reg = register.register(blockName) { -> supplier(blockProp) }

            return BlockRegistry(
                reg, blockItem { reg.value() } where blockItemBuilder,
                blockEntitySupplier
                    ?.let { be -> with(beRegister) { (blockName of be).apply(blockEntityBuilder) on { reg.value() } } })
        }

    }

    inner class BlockRegistry<T : Block>(val blockRegistry : DeferredBlock<      T      >,
                                         val  itemRegistry : DeferredItem <out BlockItem>,
                                         val    beRegistry : DeferredBET<BlockEntity>?)
    {
        operator fun getValue(obj: Any,     property: KProperty<*>) : T = blockRegistry.value()
        operator fun getValue(obj: Nothing, property: KProperty<*>) : T = blockRegistry.value()

        val key get() = blockRegistry.key
    }
}