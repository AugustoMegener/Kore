package io.kito.kore.common.world.level.block

import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.bet
import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.createBE
import io.kito.kore.common.world.level.block.entity.KBlockEntity
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import kotlin.jvm.optionals.getOrNull

abstract class KBaseEntityBlock<T : BlockEntity>(properties: Properties) : BaseEntityBlock(properties) {

    override fun codec() = TODO("Block codecs have not yet been implemented")

    override fun newBlockEntity(pos: BlockPos, state: BlockState) =
        createBE(this::class, pos, state)

    override fun <T : BlockEntity> getTicker(level: Level,
                                             state: BlockState,
                                             blockEntityType: BlockEntityType<T>) =
        createTickerHelper(blockEntityType, bet(this::class)) { _, _, _, be -> if (be is KBlockEntity) be.tick() }

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos) =
        level.getBlockEntity(pos) as? MenuProvider

    override fun useWithoutItem(state: BlockState,
                                level: Level,
                                pos: BlockPos,
                                player: Player,
                                hitResult: BlockHitResult) : InteractionResult
    {
        if (player is ServerPlayer) state.getMenuProvider(level, pos)
            ?.let { p -> player.openMenu(p) { it.writeBlockPos(pos) } }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun onRemove(state: BlockState,
                          level: Level,
                          pos: BlockPos,
                          newState: BlockState,
                          movedByPiston: Boolean)
    {
        val be = level.getBlockEntity(pos, bet(this::class)).getOrNull()

        if (be != null && be is KBlockEntity && !level.isClientSide) {
            Containers.dropContents(level, pos, be.itemDrops)
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    @Suppress(UNCHECKED_CAST)
    fun blockEntity(level: Level, pos: BlockPos) = level.getBlockEntity(pos) as T

    @Suppress(UNCHECKED_CAST)
    fun <R> withBlockEntity(level: Level, pos: BlockPos, block: (T) -> R) = with(level.getBlockEntity(pos) as T, block)
}