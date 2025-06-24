package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

/**
 * A registry for managing and registering custom block entity capabilities in Kore.
 * Annotated with `@Scan` to be automatically discovered by Kore for capability registration.
 * This object collects block entity capability suppliers and registers them with NeoForge during the appropriate event.
 */
@Scan
object BlockEntityCapRegister {

    /**
     * A list of pairs, where each pair consists of a supplier for a [BlockEntityType] and a list of [BECapRegistry] instances.
     * Capabilities added to this list will be registered with NeoForge for the specified block entity type.
     */
    val beCaps = arrayListOf<Pair<() -> BlockEntityType<*>, List<BECapRegistry<*, *, *, *>>>>()

    /**
     * Data class representing a single block entity capability registration entry.
     *
     * @param B The type of the [BlockEntity] that this capability applies to.
     * @param O The type of the capability object.
     * @param C The type of the context object for the capability.
     * @param T The type of the [BlockCapability].
     * @property cap The [BlockCapability] instance to be registered.
     * @property capSupplier The [ICapabilityProvider] that provides the capability instance for the block entity.
     */
    data class BECapRegistry<B : BlockEntity, O, C, T : BlockCapability<O, C>>(val cap         : T,
                                                                               val capSupplier : ICapabilityProvider<B, C, O>)

    /**
     * Event subscriber method that registers all collected block entity capabilities with NeoForge.
     * Annotated with `@KSubscribe` to be automatically invoked by Kore's event system.
     * This method listens for the [RegisterCapabilitiesEvent] event.
     *
     * @param event The [RegisterCapabilitiesEvent] event, provided by NeoForge.
     */
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

