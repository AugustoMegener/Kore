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

@Scan
object ItemCapRegister {

    val itemCaps = arrayListOf<Pair<() -> ItemLike, List<ItemCapRegistry<*, *, *>>>>()

    data class ItemCapRegistry<O, C, T : ItemCapability<O, C>>(val cap         : T,
                                                               val capSupplier : ICapabilityProvider<ItemStack, C?, O>)

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