//@file:SubscribesDist(DEDICATED_SERVER)

package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.event.KSubscriptionsOn
import net.neoforged.api.distmarker.Dist.DEDICATED_SERVER
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent

@KMod
fun init() {

}

@KSubscriptionsOn(DEDICATED_SERVER)
object Foo {

    @KSubscribe
    fun FMLCommonSetupEvent.onLoad() {
        logger.info("$ID loaded!")
    }
}