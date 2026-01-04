package io.kito.kore.common.world.level.block.entity


import io.kito.kore.common.data.nbt.KValueIOSerializable
import io.kito.kore.common.registry.BlockEntityTypeRegister.Companion.bet
import io.kito.kore.util.minecraft.nbtOps
import io.kito.kore.util.minecraft.set
import io.kito.kore.util.neoforge.BlockEntityExt.beLvl
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.util.ProblemReporter
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrNull

abstract class KBlockEntity(pos: BlockPos, blockState: BlockState, type: BlockEntityType<*>? = null)
    : BlockEntity(type ?: bet(blockState.block::class), pos, blockState), KValueIOSerializable
{
    open val itemDrops = NonNullList.create<ItemStack>()

    override fun saveAdditional(output: ValueOutput)
        { serialize(output.child("data"))
          super.saveAdditional(output) }

    override fun loadAdditional(input: ValueInput)
        { deserialize(input.childOrEmpty("data"))
          super.loadAdditional(input) }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
         super.getUpdateTag(registries).also { tag -> 
             tag["data"] = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries)
                 .also { serialize(it) }.buildResult()
         }

    override fun handleUpdateTag(input: ValueInput)
        { deserialize(input.childOrEmpty("data"))
          super.handleUpdateTag(input) }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun onDataPacket(net: Connection, valueInput: ValueInput)
        { deserialize(valueInput.childOrEmpty("data"))
          super.onDataPacket(net, valueInput) }


    open fun tick() {}


    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {

        Containers.dropContents(beLvl, pos, itemDrops)


        super.preRemoveSideEffects(pos, state)
    }
}