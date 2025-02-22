package io.kito.kore.util

import io.kito.kore.Kore.ID
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforgespi.language.IModInfo
import net.neoforged.neoforgespi.locating.IModFile


val modList: ModList get() = ModList.get()

val IModFile.info: IModInfo get() = modInfos.first()
val IModFile.modId: String  get() = modContainer.modId

inline val blockItemHandler: BlockCapability<IItemHandler, Direction?>
    get() = Capabilities.ItemHandler.BLOCK

inline val stackItemHandler: ItemCapability<IItemHandler, Void?>
    get() = Capabilities.ItemHandler.ITEM

inline val entityItemHandler: EntityCapability<IItemHandler, Void?>
    get() = Capabilities.ItemHandler.ENTITY

inline val autoEntityItemHandler: EntityCapability<IItemHandler, Direction?>
    get() = Capabilities.ItemHandler.ENTITY_AUTOMATION

inline val blockFluidHandler: BlockCapability<IFluidHandler, Direction?>
    get() = Capabilities.FluidHandler.BLOCK

inline val stackFluidHandler: ItemCapability<IFluidHandlerItem, Void?>
    get() = Capabilities.FluidHandler.ITEM

inline val entityFluidHandler: EntityCapability<IFluidHandler, Direction?>
    get() = Capabilities.FluidHandler.ENTITY

inline val blockEnergStorage: BlockCapability<IEnergyStorage, Direction?>
    get() = Capabilities.EnergyStorage.BLOCK

inline val stackEnergStorage: ItemCapability<IEnergyStorage, Void?>
    get() = Capabilities.EnergyStorage.ITEM

inline val entityEnergStorage: EntityCapability<IEnergyStorage, Direction?>
    get() = Capabilities.EnergyStorage.ENTITY

fun forEachModContainer(action: ModContainer.(String) -> Unit) { modList.forEachModContainer { id, c -> action(c, id) } }

fun forEachModFile(action: IModFile.() -> Unit) { modList.forEachModFile { it.apply(action) } }

fun forEachKoreUserFile(action: IModFile.() -> Unit) {
    modList.forEachModFile { file ->
        if (file.info.modId == ID || ID in file.info.dependencies.map { it.modId }) file.apply(action)
    }
}

val IModFile.modContainer: ModContainer get() = modList.getModContainerById(info.modId).get()

operator fun IItemHandlerModifiable.set(idx: Int, value: ItemStack) { setStackInSlot(idx, value) }
operator fun IItemHandler.get(idx: Int): ItemStack = getStackInSlot(idx)


fun stackHandlerOf(vararg stack: ItemStack) = ItemStackHandler(NonNullList.of(ItemStack.EMPTY, *stack))
fun stackHandlerOf(size: Int, vararg stacks: Pair<Int, ItemStack>) =
    ItemStackHandler(NonNullList.withSize(size, ItemStack.EMPTY))
        .also { stacks.forEach { (i, s) -> it[i] = s } }

inline val BlockEntity.beLvl get() = level!!

inline fun <T, C : Any, R> ItemStack.withCapability(cap: ItemCapability<T, C>, ctx: C, block: (T) -> R) =
    block(getCapability(cap, ctx)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T, R>  ItemStack.withCapability(cap: ItemCapability<T, Void?>, block: (T) -> R) =
    block(getCapability(cap)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T, C : Any, R> BlockEntity.withCapability(cap: BlockCapability<T, C>, ctx: C, block: (T) -> R) =
    block(beLvl.getCapability(cap, blockPos, blockState, this, ctx)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T, R>  BlockEntity.withCapability(cap: BlockCapability<T, Void?>, block: (T) -> R) =
    block(beLvl.getCapability(cap, blockPos, blockState, this)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T, C : Any, R> Level.withCapabilityOn(pos: BlockPos, cap: BlockCapability<T, C>, ctx: C, block: (T) -> R) =
    block(getCapability(cap, pos, ctx)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T : Any, R>  Level.withCapabilityOn(pos: BlockPos, cap: BlockCapability<T, Void?>, block: (T) -> R) =
    block(getCapability(cap, pos)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))


inline fun <T, C : Any, R> Entity.withCapability(cap: EntityCapability<T, C>, ctx: C, block: (T) -> R) =
    block(getCapability(cap, ctx)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))

inline fun <T, R>  Entity.withCapability(cap: EntityCapability<T, Unit>, block: (T) -> R) =
    block(getCapability(cap, Unit)
        ?: throw IllegalArgumentException("there is no capability ${cap::class} for ${this::class}"))