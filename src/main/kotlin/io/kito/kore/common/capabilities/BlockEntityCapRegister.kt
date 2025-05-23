package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

@Scan
object BlockEntityCapRegister {

    val beCaps = arrayListOf<Pair<() -> BlockEntityType<*>, List<BECapRegistry<*, *, *, *>>>>()

    data class BECapRegistry<B : BlockEntity, O, C, T : BlockCapability<O, C>>(val cap         : T,
                                                                               val capSupplier : ICapabilityProvider<B, C, O>)

    @KSubscribe
    @Suppress(UNCHECKED_CAST)
    fun RegisterCapabilitiesEvent.onRegisterCaps() {
        beCaps.forEach { (sbet, regs) ->
            val bet = sbet()
            regs.forEach {
                registerBlockEntity(
                    it.cap as BlockCapability<Any, Any>,
                    bet as BlockEntityType<BlockEntity>,
                    it.capSupplier as ICapabilityProvider<BlockEntity, Any, Any>
                )
            }
        }
    }
}