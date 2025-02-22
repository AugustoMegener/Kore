package io.kito.kore.common.world.level.block.entity


import io.kito.kore.common.data.nbt.KNBTSerializable
import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.bet
import io.kito.kore.util.set
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KProperty

abstract class KBlockEntity(pos: BlockPos, blockState: BlockState, type: BlockEntityType<*>? = null)
    : BlockEntity(type ?: bet(blockState.block::class), pos, blockState), KNBTSerializable
{
    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider)
        { tag["data"] = serializeNBT(registries)
          super.saveAdditional(tag, registries) }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider)
        { deserializeNBT(registries, tag["data"] as CompoundTag)
          super.loadAdditional(tag, registries) }

    override fun getUpdateTag(registries: HolderLookup.Provider) =
         CompoundTag().also { it["data"] = serializeNBT(registries) }

    override fun handleUpdateTag(tag: CompoundTag, registries: HolderLookup.Provider)
        { deserializeNBT(registries, tag["data"] as CompoundTag)
          super.handleUpdateTag(tag, registries) }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun onDataPacket(net: Connection, pkt: ClientboundBlockEntityDataPacket, registries: HolderLookup.Provider)
        { deserializeNBT(registries, pkt.tag["data"] as CompoundTag)
          super.onDataPacket(net, pkt, registries) }

    final override fun   serializeNBT(provider: HolderLookup.Provider) = super.serializeNBT(provider)
    final override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag)
        { super.deserializeNBT(provider, nbt) }

    open fun tick() {}

    class AutoDirt<T>(private var data: T) {
        operator fun getValue(be: BlockEntity, prop: KProperty<*>) = data
        operator fun setValue(be: BlockEntity, prop: KProperty<*>, new: T) { data = new; be.setChanged() }
    }
}