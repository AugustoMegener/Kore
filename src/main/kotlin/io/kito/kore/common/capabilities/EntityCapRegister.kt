package io.kito.kore.common.capabilities

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

@Scan
object EntityCapRegister {

    val entityCaps = arrayListOf<Pair<() -> EntityType<*>, List<EntityCapRegistry<*, *, *>>>>()

    data class EntityCapRegistry<O, C, T : EntityCapability<O, C>>(val cap         : T,
                                                                   val capSupplier : ICapabilityProvider<Entity, C?, O>)

    @KSubscribe
    @Suppress(UNCHECKED_CAST)
    fun RegisterCapabilitiesEvent.onRegisterCaps() {
        entityCaps.forEach { (sEntity, regs) ->
            regs.forEach {
                registerEntity(
                    it.cap as EntityCapability<Any, Any>,
                    sEntity(),
                    it.capSupplier as ICapabilityProvider<Entity, Any, Any>
                )
            }
        }
    }
}