package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

/**
 * A registry for managing and registering custom item capabilities in Kore.
 * Annotated with `@Scan` to be automatically discovered by Kore for capability registration.
 * This object collects item capability suppliers and registers them with NeoForge during the appropriate event.
 */
@Scan
object ItemCapRegister {

    /**
     * A list of pairs, where each pair consists of a supplier for an [ItemLike] and a list of [ItemCapRegistry] instances.
     * Capabilities added to this list will be registered with NeoForge for the specified item.
     */
    val itemCaps = arrayListOf<Pair<() -> ItemLike, List<ItemCapRegistry<*, *, *>>>>()

    /**
     * Data class representing a single item capability registration entry.
     *
     * @param O The type of the capability object.
     * @param C The type of the context object for the capability.
     * @param T The type of the [ItemCapability].
     * @property cap The [ItemCapability] instance to be registered.
     * @property capSupplier The [ICapabilityProvider] that provides the capability instance for the item stack.
     */
    data class ItemCapRegistry<O, C, T : ItemCapability<O, C>>(val cap         : T,
                                                               val capSupplier : ICapabilityProvider<ItemStack, C?, O>)

    /**
     * Event subscriber method that registers all collected item capabilities with NeoForge.
     * Annotated with `@KSubscribe` to be automatically invoked by Kore's event system.
     * This method listens for the [RegisterCapabilitiesEvent] event.
     *
     * @param event The [RegisterCapabilitiesEvent] event, provided by NeoForge.
     */
    @KSubscribe
    @Suppress(UNCHECKED_CAST)
    fun RegisterCapabilitiesEvent.onRegisterCaps() {
        itemCaps.forEach { (sItem, regs) ->
            regs.forEach {
                registerItem(
                    it.cap as ItemCapability<Any, Any>,
                    it.capSupplier as ICapabilityProvider<ItemStack, Any, Any>,
                    sItem()
                )
            }
        }
    }
}

