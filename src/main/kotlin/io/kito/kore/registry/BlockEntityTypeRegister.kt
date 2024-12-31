package io.kito.kore.registry

import com.mojang.datafixers.types.Type
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

typealias BlockEntitySupplier = (BlockPos, BlockState) -> BlockEntity
typealias DeferredBET<T> = DeferredHolder<BlockEntityType<*>, BlockEntityType<T>>

open class BlockEntityTypeRegister(final override val id: String) : AutoRegister {

    private val registry = DeferredRegister.create(BLOCK_ENTITY_TYPE, id)

    infix fun <T : BlockEntity> String.of(builder: (BlockPos, BlockState) -> T) = BETBuilder(this, builder)

    inner class BETBuilder<T : BlockEntity>(val name: String, val supplier: (BlockPos, BlockState) -> T) {

        private var type: Type<*>? = null

        infix fun onAll(blocks: () -> Array<out Block>) =
            registry.register(name) { -> BlockEntityType.Builder.of(supplier, *blocks()).build(type) }

        infix fun on(block: () -> Block) = onAll { arrayOf(block()) }

        infix fun withType(value: Type<*>?) = also { type = value }
    }

    override fun register(bus: IEventBus) { registry.register(bus) }
}