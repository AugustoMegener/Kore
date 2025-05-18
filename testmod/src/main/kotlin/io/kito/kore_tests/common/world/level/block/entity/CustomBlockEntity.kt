package io.kito.kore_tests.common.world.level.block.entity

import io.kito.kore.common.data.Save
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
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.jvm.optionals.getOrNull

class CustomBlockEntity(pos: BlockPos, blockState: BlockState) : KBlockEntity(pos, blockState), MenuProvider {

    @Save var name by AutoDirt("nothing")

    @Save val inventory = object : ItemStackHandler(3) {

        private val input1Slot = 0
        private val input2Slot = 1
        private val outputSlot = 2

        val recipeInput = NiceRecipeInput(this, input1Slot, input2Slot)

        val actualRecipe
            get() = beLvl.recipeManager.getRecipeFor(NiceRecipe.type, recipeInput, beLvl).getOrNull()?.value

        override fun isItemValid(slot: Int, stack: ItemStack) =
            when(slot) { outputSlot -> get(slot).isEmpty
                         else -> true }

        override fun onContentsChanged(slot: Int) {
            setChanged()

            if (slot != outputSlot) updateOutput()
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == outputSlot && !simulate) {
                actualRecipe?.assemble(recipeInput, beLvl.registryAccess())

                val stack = super.extractItem(slot, amount, false)
                updateOutput()
                return stack
            }

            return super.extractItem(slot, amount, simulate)
        }

        fun updateOutput() {
            setStackInSlot(outputSlot, actualRecipe?.getResultItem(beLvl.registryAccess()) ?: ItemStack.EMPTY)
        }
    }

    override val itemDrops get() = NonNullList.copyOf((0..1).map { inventory[it] })

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player) =
        CustomMenu(containerId, playerInventory, this)

    override fun getDisplayName() = name.toTitle().literal
}