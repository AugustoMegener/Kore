package io.kito.kore_tests.common.world.level.block.entity

import io.kito.kore.common.data.Save
import io.kito.kore.common.data.strategy.NBTSerialization
import io.kito.kore.common.data.strategy.SerializationStrategy
import io.kito.kore.common.world.level.block.entity.KBlockEntity
import io.kito.kore.util.minecraft.literal
import io.kito.kore.util.neoforge.BlockEntityExt.AutoDirt
import io.kito.kore.util.neoforge.BlockEntityExt.beLvl
import io.kito.kore.util.neoforge.ItemHandlerExt.get
import io.kito.kore.util.toTitle
import io.kito.kore_tests.common.world.inventory.CustomMenu
import io.kito.kore_tests.common.world.item.crafting.NiceRecipe
import io.kito.kore_tests.common.world.item.crafting.NiceRecipeInput
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import kotlin.jvm.optionals.getOrNull

class CustomBlockEntity(pos: BlockPos, blockState: BlockState) : KBlockEntity(pos, blockState), MenuProvider {

    @Save var name by AutoDirt("nothing")

    @Save val inventory = object : ItemStacksResourceHandler(3) {

        private val input1Slot = 0
        private val input2Slot = 1
        private val outputSlot = 2

        val recipeInput = NiceRecipeInput(this, input1Slot, input2Slot)

        val actualRecipe
            get() = (beLvl as ServerLevel).recipeAccess().getRecipeFor(NiceRecipe.type, recipeInput, beLvl).getOrNull()?.value



        override fun isValid(index: Int, resource: ItemResource) = when(index) { outputSlot -> get(index).isEmpty
            else -> true }

        override fun onContentsChanged(index: Int, previousContents: ItemStack) {
            setChanged()

            if (index != outputSlot) updateOutput(actualRecipe?.result!!)
        }

        override fun extract(index: Int, resource: ItemResource, amount: Int, transaction: TransactionContext): Int {
            if (index == outputSlot) {
                val result = actualRecipe?.assemble(recipeInput, beLvl.registryAccess()) ?: return 0

                val stack = super.extract(index, resource, amount, transaction)
                updateOutput(result)
                return stack
            }

            return super.extract(index, resource, amount, transaction)
        }

        fun updateOutput(result: ItemStack) {

            set(outputSlot, ItemResource.of(result), result.count)
        }
    }

    override val itemDrops: NonNullList<ItemStack> get() = NonNullList.copyOf((0..1).map {
        inventory[it].toStack(inventory.getAmountAsInt(it))
    })

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player) =
        CustomMenu(containerId, playerInventory, this)

    override fun getDisplayName() = name.toTitle().literal


    override val strategy by lazy { NBTSerialization(beLvl.registryAccess()) }
}