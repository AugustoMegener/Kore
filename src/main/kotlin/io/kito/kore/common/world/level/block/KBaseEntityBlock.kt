package io.kito.kore.common.world.level.block

import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.bet
import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.createBE
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.common.world.level.block.entity.KBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KProperty

abstract class KBaseEntityBlock<T : BlockEntity>(properties: Properties) : BaseEntityBlock(properties) {

    override fun codec() = TODO("Block codecs have not yet been implemented")

    override fun newBlockEntity(pos: BlockPos, state: BlockState) =
        createBE(this::class, pos, state)

    override fun <T : BlockEntity> getTicker(level: Level,
                                             state: BlockState,
                                             blockEntityType: BlockEntityType<T>) =
        createTickerHelper(blockEntityType, bet(this::class)) { _, _, _, be -> if (be is KBlockEntity) be.tick() }

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    @Suppress(UNCHECKED_CAST)
    fun blockEntity(level: Level, pos: BlockPos) = level.getBlockEntity(pos) as T

    @Suppress(UNCHECKED_CAST)
    fun <R> withBlockEntity(level: Level, pos: BlockPos, block: (T) -> R) = with(level.getBlockEntity(pos) as T, block)


}