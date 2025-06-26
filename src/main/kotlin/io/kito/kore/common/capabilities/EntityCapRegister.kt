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

/**
 * A registry for managing and registering custom entity capabilities in Kore.
 * Annotated with `@Scan` to be automatically discovered by Kore for capability registration.
 * This object collects entity capability suppliers and registers them with NeoForge during the appropriate event.
 */
@Scan
object EntityCapRegister {

    /**
     * A list of pairs, where each pair consists of a supplier for an [EntityType] and a list of [EntityCapRegistry] instances.
     * Capabilities added to this list will be registered with NeoForge for the specified entity type.
     */
    val entityCaps = arrayListOf<Pair<() -> EntityType<*>, List<EntityCapRegistry<*, *, *>>>>()

    /**
     * Data class representing a single entity capability registration entry.
     *
     * @param O The type of the capability object.
     * @param C The type of the context object for the capability.
     * @param T The type of the [EntityCapability].
     * @property cap The [EntityCapability] instance to be registered.
     * @property capSupplier The [ICapabilityProvider] that provides the capability instance for the entity.
     */
    data class EntityCapRegistry<O, C, T : EntityCapability<O, C>>(val cap         : T,
                                                                   val capSupplier : ICapabilityProvider<Entity, C?, O>)

    /**
     * Event subscriber method that registers all collected entity capabilities with NeoForge.
     * Annotated with `@KSubscribe` to be automatically invoked by Kore's event system.
     * This method listens for the [RegisterCapabilitiesEvent] event.
     *
     * @param event The [RegisterCapabilitiesEvent] event, provided by NeoForge.
     */
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

