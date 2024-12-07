package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.event.KSubscriptionsOn
import io.kito.kore.common.registry.AutoRegister
import io.kito.kore.common.registry.KRegister
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.api.distmarker.Dist.DEDICATED_SERVER

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent


object BlockRegister : AutoRegister<Block>("kore_test", BLOCK)

@KRegister(BlockRegister::class)
val block = Block(BlockBehaviour.Properties.of())

@KMod
fun init() {

}


@KSubscribe
fun FMLCommonSetupEvent.onLoad() {
    logger.info("$ID loaded!")
}

