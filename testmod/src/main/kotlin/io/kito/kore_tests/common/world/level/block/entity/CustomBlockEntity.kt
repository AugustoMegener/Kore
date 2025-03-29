package io.kito.kore_tests.common.world.level.block.entity

import io.kito.kore.common.data.Save
import io.kito.kore.common.world.level.block.entity.KBlockEntity
import io.kito.kore.util.neoforge.BlockEntityExt
import io.kito.kore.util.neoforge.BlockEntityExt.beStackHandler

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class CustomBlockEntity(pos: BlockPos, blockState: BlockState) : KBlockEntity(pos, blockState) {

    @Save
    var name by BlockEntityExt.AutoDirt("nothing")

    @Save
    val inventory = beStackHandler()
}