package io.kito.kore.common.registry

import io.kito.kore.Kore.ID
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties.of as properties

object BlockRegister : AutoRegister<Block>(ID) {

    val block = "block" { Block(properties()) }
}