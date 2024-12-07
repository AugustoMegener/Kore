//@file:SubscribesDist(DEDICATED_SERVER)

package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.event.Subscribe
import io.kito.kore.common.event.SubscriptionsDist
import net.neoforged.api.distmarker.Dist.DEDICATED_SERVER
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent

@KMod
fun init() {

}

@SubscriptionsDist(DEDICATED_SERVER)
object Foo {

    @Subscribe
    fun FMLCommonSetupEvent.onLoad() {
        logger.info("$ID loaded!")
    }
}