package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.registry.ItemRegister
import net.minecraft.world.item.Item
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent

@KMod
fun init() {

}

@KSubscribe
fun FMLCommonSetupEvent.onLoad() {
    logger.info("$ID loaded!")
}

object Blocks : ItemRegister(ID) {

    val item = "item" { Item(it) } where {  }
}
