package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

/**
 * A registry for managing and registering custom block capabilities in Kore.
 * Annotated with `@Scan` to be automatically discovered by Kore for capability registration.
 * This object collects block capability suppliers and registers them with NeoForge during the appropriate event.
 */
@Scan
object BlockCapRegister {

    /**
     * A list of pairs, where each pair consists of a supplier for a [Block] and a list of [BlockCapRegistry] instances.
     * Capabilities added to this list will be registered with NeoForge for the specified block.
     */
    val blockCaps = arrayListOf<Pair<() -> Block, List<BlockCapRegistry<*, *, *>>>>()

    /**
     * Data class representing a single block capability registration entry.
     *
     * @param O The type of the capability object.
     * @param C The type of the context object for the capability.
     * @param T The type of the [BlockCapability].
     * @property cap The [BlockCapability] instance to be registered.
     * @property capSupplier The [IBlockCapabilityProvider] that provides the capability instance for the block.
     */
    data class BlockCapRegistry<O, C, T : BlockCapability<O, C>>(val cap         : T,
                                                                 val capSupplier : IBlockCapabilityProvider<O, C>)

    /**
     * Event subscriber method that registers all collected block capabilities with NeoForge.
     * Annotated with `@KSubscribe` to be automatically invoked by Kore's event system.
     * This method listens for the [RegisterCapabilitiesEvent] event.
     *
     * @param event The [RegisterCapabilitiesEvent] event, provided by NeoForge.
     */
    @KSubscribe
    @Suppress(UNCHECKED_CAST)
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

