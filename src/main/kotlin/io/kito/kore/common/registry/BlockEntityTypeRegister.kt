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

/**
 * A utility class for registering custom [BlockEntityType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of block entity types
 * with NeoForge, simplifying the process of adding custom block entities to the game.
 *
 * @property id The mod ID or namespace for these block entity types.
 */
open class BlockEntityTypeRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [BlockEntityType]s, tied to the given mod ID.
     */
    private val registry = DeferredRegister.create(BLOCK_ENTITY_TYPE, id)

    /**
     * Infix function to define a new [BlockEntityType] with a supplier for creating [BlockEntity] instances.
     * This is the first step in a chain to addEntry a block entity type.
     *
     * @param T The type of the [BlockEntity] that this type will create.
     * @param name The name of the block entity type (e.g., "my_block_entity").
     * @param builder A lambda that supplies a new instance of the [BlockEntity] given a [BlockPos] and [BlockState].
     * @return A [BETBuilder] to continue the registration process.
     */
    infix fun <T : BlockEntity> String.of( builder: (BlockPos, BlockState) -> T) =
        BETBuilder(this, builder)

    /**
     * Inner class to facilitate the building and registration of [BlockEntityType]s.
     *
     * @param T The type of the [BlockEntity] that this builder handles.
     * @property name The name of the block entity type.
     * @property supplier A lambda that supplies a new instance of the [BlockEntity].
     */
    inner class BETBuilder<T : BlockEntity>(val name: String,
                                            val supplier: (BlockPos, BlockState) -> T)
    {
        private val caps = BECaps()

        private var renderer: BlockEntityRenderer<T, *>? = null

        infix fun onAll(blocks: Collection<() -> Block>): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> =
            registry.register(name) { ->
                BlockEntityType(
                    supplier,
                    blocks.map { it() }.toSet()
                )
            }
                .also {
                    bets += blocks to it::get
                    beCaps += it::get to caps.registries
                    renderer?.let { r -> RendererRegistry.blockEntityRenderers += it::value to r }
                }


        /**
         * Specifies a single block that this [BlockEntityType] is valid for.
         * @param block A lambda that supplies the [Block] that this block entity type can be associated with.
         * @return A [DeferredHolder] for the registered [BlockEntityType].
         */
        infix fun on(block: () -> Block) = onAll(listOf(block))

        /**
         * Configures capabilities for this block entity type.
         * @param adder A lambda that takes a [BECaps] instance and adds capabilities to it.
         * @return This [BETBuilder] for fluent chaining.
         */
        infix fun withCaps(adder: BECaps.() -> Unit) = also { caps.apply(adder) }

        /**
         * Sets the [BlockEntityRenderer] for this block entity type.
         * @param beRenderer The [BlockEntityRenderer] for this block entity type.
         * @return This [BETBuilder] for fluent chaining.
         */
        infix fun withRenderer(beRenderer: BlockEntityRenderer<T, *>) = also { renderer = beRenderer }

        /**
         * Inner class for defining and collecting block entity capabilities.
         */
        inner class BECaps {
            /**
             * A list of [BECapRegistry] instances, each representing a capability to be registered.
             */
            val registries = arrayListOf<BECapRegistry<*, *, *, *>>()

            /**
             * Operator function to add a capability with a context object.
             * @param O The type of the capability object.
             * @param C The type of the context object.
             * @param getter A lambda that takes the [BlockEntity] and the context object, and returns the capability object.
             */
            operator fun <O, C> BlockCapability<O, C    >.invoke(getter: T.(C) -> O)
                { registries += BECapRegistry<T, O, C, BlockCapability<O, C>>(this) { x, y -> getter(x, y) } }

            /**
             * Operator function to add a capability without a context object (Void?).
             * @param O The type of the capability object.
             * @param getter A lambda that takes the [BlockEntity] and returns the capability object.
             */
            operator fun <O   > BlockCapability<O, Void?>.invoke(getter: T.() -> O)
                { registries += BECapRegistry(this) { b: T, _ -> getter(b) } }
        }
    }

    /**
     * Registers the [DeferredRegister] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined block entity types.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) { registry.register(bus) }

    /**
     * Companion object providing utility functions related to [BlockEntityType]s.
     */
    companion object {
        /**
         * A list of pairs, where each pair consists of a supplier for an array of [Block]s
         * and a supplier for their corresponding [BlockEntityType].
         */
        private val bets = arrayListOf<Pair<Collection<() -> Block>, () -> BlockEntityType<*>>>()

        /**
         * A lazily initialized immutable map that maps [KClass] of [Block] to a supplier for its [BlockEntityType].
         * This provides a quick lookup for the block entity type associated with a given block class.
         */
        val blockEntityTypes: ImmutableMap<KClass<out Block>, () -> BlockEntityType<*>> by lazy {
            ImmutableMap.copyOf(bets.flatMap { (bks, bet) -> bks.map { it()::class to bet } }. toMap())
        }

        /**
         * Retrieves the [BlockEntityType] for a given [Block] class.
         * @param clazz The [KClass] of the [Block].
         * @return The [BlockEntityType] associated with the block.
         * @throws NullPointerException if no [BlockEntityType] is found for the given block class.
         */
        fun bet(clazz: KClass<out Block>) = blockEntityTypes[clazz]!!()

        /**
         * Creates a new [BlockEntity] instance for a given [Block] class, [BlockPos], and [BlockState].
         * @param clazz The [KClass] of the [Block].
         * @param pos The [BlockPos] where the block entity is located.
         * @param state The [BlockState] of the block.
         * @return A new [BlockEntity] instance.
         */
        fun createBE(clazz: KClass<out Block>, pos: BlockPos, state: BlockState) = bet(clazz).create(pos, state)
    }
}

