package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

@Scan
object BlockCapRegister {

    val blockCaps = arrayListOf<Pair<() -> Block, List<BlockCapRegistry<*, *, *>>>>()

    data class BlockCapRegistry<O, C, T : BlockCapability<O, C>>(val cap         : T,
                                                                 val capSupplier : IBlockCapabilityProvider<O, C>)

    @KSubscribe
    fun RegisterCapabilitiesEvent.onRegisterCaps() {
        blockCaps.forEach { (sblock, regs) ->
            regs.forEach {
                registerBlock(
                    it.cap as BlockCapability<Any, Any>,
                    it.capSupplier as IBlockCapabilityProvider<Any, Any>,
                    sblock()
                )
            }
        }
    }
}