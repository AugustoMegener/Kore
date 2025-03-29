package io.kito.kore.common.registry

import com.google.common.collect.ImmutableMap
import com.mojang.datafixers.types.Type
import io.kito.kore.client.renderer.RendererRegistry
import io.kito.kore.common.capabilities.BlockEntityCapRegister.BECapRegistry
import io.kito.kore.common.capabilities.BlockEntityCapRegister.beCaps
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KClass

typealias BlockEntitySupplier = (BlockPos, BlockState) -> BlockEntity
typealias DeferredBET<T> = DeferredHolder<BlockEntityType<*>, BlockEntityType<T>>

open class BlockEntityTypeRegister(final override val id: String) : AutoRegister {

    private val registry = DeferredRegister.create(BLOCK_ENTITY_TYPE, id)

    infix fun <T : BlockEntity> String.of( builder: (BlockPos, BlockState) -> T) =
        BETBuilder(this, builder)

    inner class BETBuilder<T : BlockEntity>(val name: String,
                                            val supplier: (BlockPos, BlockState) -> T)
    {
        private var type: Type<*>? = null
        private val caps = BECaps()

        private var renderer: BlockEntityRenderer<T>? = null

        infix fun onAll(blocks: () -> Array<out Block>): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> =
            registry.register(name) { -> BlockEntityType(supplier, blocks().toMutableSet(), type) }
                .also {
                    bets += blocks to it::get
                    beCaps += it::get to caps.registries
                    renderer?.let { r -> RendererRegistry.blockEntityRenderers += it::value to r }
                }


        infix fun on(block: () -> Block) = onAll { arrayOf(block()) }

        infix fun withType(value: Type<*>?) = also { type = value }

        infix fun withCaps(adder: BECaps.() -> Unit) = also { caps.apply(adder) }

        infix fun withRenderer(beRenderer: BlockEntityRenderer<T>) = also { renderer = beRenderer }

        inner class BECaps {
            val registries = arrayListOf<BECapRegistry<*, *, *, *>>()

            operator fun <O, C> BlockCapability<O, C    >.invoke(getter: T.(C) -> O)
                { registries += BECapRegistry<T, O, C, BlockCapability<O, C>>(this) { x, y -> getter(x, y) } }

            operator fun <O   > BlockCapability<O, Void?>.invoke(getter: T.() -> O)
                { registries += BECapRegistry(this) { b: T, _ -> getter(b) } }
        }
    }

    override fun register(bus: IEventBus) { registry.register(bus) }

    companion object {
        private val bets = arrayListOf<Pair<() -> Array<out Block>, () -> BlockEntityType<*>>>()

        val blockEntityTypes: ImmutableMap<KClass<out Block>, () -> BlockEntityType<*>> by lazy {
            ImmutableMap.copyOf(bets.flatMap { (bks, bet) -> bks().map { it::class to bet } }. toMap())
        }

        fun bet(clazz: KClass<out Block>) = blockEntityTypes[clazz]!!()

        fun createBE(clazz: KClass<out Block>, pos: BlockPos, state: BlockState) = bet(clazz).create(pos, state)
    }
}