package io.kito.kore.util.neoforge

import io.kito.kore.util.neoforge.BlockEntityExt.beLvl
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.EnergyHandler
import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.item.ItemResource

/**
 * Utility object for accessing common NeoForge capabilities.
 * This object provides convenient inline properties and functions to retrieve various capabilities
 * from [ItemStack]s, [BlockEntity]s, [Level]s, and [Entity]s.
 */
object Capability {
    /**
     * Provides the [BlockCapability] for [BlockCapability<ResourceHandler<ItemResource>] on blocks.
     */
    inline val blockItemHandler: BlockCapability<ResourceHandler<ItemResource>, Direction?>
        get() = Capabilities.Item.BLOCK

    /**
     * Provides the [ItemCapability] for [IItemHandler] on item stacks.
     */
    inline val stackItemHandler: ItemCapability<ResourceHandler<ItemResource>, ItemAccess?>
        get() = Capabilities.Item.ITEM

    /**
     * Provides the [EntityCapability] for [IItemHandler] on entities.
     */
    inline val entityItemHandler: EntityCapability<ResourceHandler<ItemResource>, Void?>
        get() = Capabilities.Item.ENTITY

    /**
     * Provides the [EntityCapability] for [IItemHandler] for automated entity interactions.
     */
    inline val autoEntityItemHandler: EntityCapability<ResourceHandler<ItemResource>, Direction?>
        get() = Capabilities.Item.ENTITY_AUTOMATION

    /**
     * Provides the [BlockCapability] for [IFluidHandler] on blocks.
     */
    inline val blockFluidHandler: BlockCapability<ResourceHandler<FluidResource>, Direction?>
        get() = Capabilities.Fluid.BLOCK

    /**
     * Provides the [ItemCapability] for [IFluidHandlerItem] on item stacks.
     */
    inline val stackFluidHandler: ItemCapability<ResourceHandler<FluidResource>, ItemAccess?>
        get() = Capabilities.Fluid.ITEM

    /**
     * Provides the [EntityCapability] for [IFluidHandler] on entities.
     */
    inline val entityFluidHandler: EntityCapability<ResourceHandler<FluidResource>, Direction?>
        get() = Capabilities.Fluid.ENTITY

    /**
     * Provides the [BlockCapability] for [IEnergyStorage] on blocks.
     */
    inline val blockEnergyStorage: BlockCapability<EnergyHandler, Direction?>
        get() = Capabilities.Energy.BLOCK

    /**
     * Provides the [ItemCapability] for [IEnergyStorage] on item stacks.
     */
    inline val stackEnergyStorage: ItemCapability<EnergyHandler, ItemAccess>
        get() = Capabilities.Energy.ITEM

    /**
     * Provides the [EntityCapability] for [IEnergyStorage] on entities.
     */
    inline val entityEnergyStorage: EntityCapability<EnergyHandler, Direction?>
        get() = Capabilities.Energy.ENTITY


    /**
     * Retrieves a capability from an [ItemStack] and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param C The type of the context object for the capability.
     * @param R The return type of the [block].
     * @param cap The [ItemCapability] to retrieve.
     * @param ctx The context object for the capability.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, C : Any, R> ItemStack.withCapability(cap: ItemCapability<T, C>, ctx: C, block: (T) -> R) =
        block(getCapability(cap, ctx)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from an [ItemStack] without a context object and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param R The return type of the [block].
     * @param cap The [ItemCapability] to retrieve.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, R> ItemStack.withCapability(cap: ItemCapability<T, Void?>, block: (T) -> R) =
        block(getCapability(cap)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from a [BlockEntity] and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param C The type of the context object for the capability.
     * @param R The return type of the [block].
     * @param cap The [BlockCapability] to retrieve.
     * @param ctx The context object for the capability.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, C : Any, R> BlockEntity.withCapability(cap: BlockCapability<T, C>, ctx: C, block: (T) -> R) =
        block(beLvl.getCapability(cap, blockPos, blockState, this, ctx)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from a [BlockEntity] without a context object and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param R The return type of the [block].
     * @param cap The [BlockCapability] to retrieve.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, R> BlockEntity.withCapability(cap: BlockCapability<T, Void?>, block: (T) -> R) =
        block(beLvl.getCapability(cap, blockPos, blockState, this)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from a [Level] at a specific [BlockPos] and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param C The type of the context object for the capability.
     * @param R The return type of the [block].
     * @param pos The [BlockPos] to retrieve the capability from.
     * @param cap The [BlockCapability] to retrieve.
     * @param ctx The context object for the capability.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, C : Any, R> Level.withCapabilityOn(pos: BlockPos, cap: BlockCapability<T, C>, ctx: C, block: (T) -> R) =
        block(getCapability(cap, pos, ctx)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from a [Level] at a specific [BlockPos] without a context object and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param R The return type of the [block].
     * @param pos The [BlockPos] to retrieve the capability from.
     * @param cap The [BlockCapability] to retrieve.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T : Any, R> Level.withCapabilityOn(pos: BlockPos, cap: BlockCapability<T, Void?>, block: (T) -> R) =
        block(getCapability(cap, pos)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))


    /**
     * Retrieves a capability from an [Entity] and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param C The type of the context object for the capability.
     * @param R The return type of the [block].
     * @param cap The [EntityCapability] to retrieve.
     * @param ctx The context object for the capability.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, C : Any, R> Entity.withCapability(cap: EntityCapability<T, C>, ctx: C, block: (T) -> R) =
        block(getCapability(cap, ctx)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

    /**
     * Retrieves a capability from an [Entity] without a context object and executes a block of code with it.
     * Throws [IllegalArgumentException] if the capability is not found.
     * @param T The type of the capability.
     * @param R The return type of the [block].
     * @param cap The [EntityCapability] to retrieve.
     * @param block The lambda to execute with the retrieved capability.
     * @return The result of the [block].
     */
    inline fun <T, R> Entity.withCapability(cap: EntityCapability<T, Unit>, block: (T) -> R) =
        block(getCapability(cap, Unit)
            ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))
}

