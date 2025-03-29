package io.kito.kore_tests.common.world.level.block

import io.kito.kore.common.world.level.block.KBaseEntityBlock
import io.kito.kore.util.minecraft.BlockProp
import io.kito.kore.util.minecraft.literal
import io.kito.kore.util.neoforge.Capability.blockItemHandler
import io.kito.kore.util.neoforge.ItemHandlerExt.get
import io.kito.kore.util.neoforge.ItemHandlerExt.set
import io.kito.kore.util.minecraft.withMcClient
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.items.IItemHandlerModifiable

class CustomBlock(properties: BlockProp) : KBaseEntityBlock<CustomBlockEntity>(properties) {

    override fun useWithoutItem(state     : BlockState,
                                level     : Level,
                                pos       : BlockPos,
                                player    : Player,
                                hitResult : BlockHitResult
    ): InteractionResult
    {
        withBlockEntity(level, pos) { be ->
            be.name = player.name.string

            withMcClient { mc ->
                mc.player!!.sendSystemMessage("hii, i'm a BE! ${be.name}".literal)
                mc.player!!.sendSystemMessage("Inventory: ${be.inventory[0]}".literal)
            }
        }

        return InteractionResult.SUCCESS_NO_ITEM_USED
    }

    override fun useItemOn(stack     : ItemStack,
                           state     : BlockState,
                           level     : Level,
                           pos       : BlockPos,
                           player    : Player,
                           hand      : InteractionHand,
                           hitResult : BlockHitResult
    ): ItemInteractionResult
    {
        withBlockEntity(level, pos) { be ->

            val inv = level.getCapability(blockItemHandler, be.blockPos, be.blockState, be, Direction.DOWN)!!
                    as IItemHandlerModifiable

            inv[0].takeUnless { it.isEmpty }?.let {
                level.addFreshEntity(ItemEntity(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it))
            }

            inv[0] = player.getItemInHand(hand)
            player.setItemInHand(hand, ItemStack.EMPTY)


            withMcClient { mc ->
                mc.player!!.sendSystemMessage("Inventory: ${inv[0]}".literal)
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun stepOn(level: Level, pos: BlockPos, state: BlockState, entity: Entity) {
        withBlockEntity(level, pos) { be ->
            withMcClient { mc ->
                val inv = level.getCapability(blockItemHandler, be.blockPos, be.blockState, be, Direction.DOWN)!!

                mc.player!!.sendSystemMessage("hii, i'm a BE! ${be.name}".literal)
                mc.player!!.sendSystemMessage("Inventory: ${inv[0]}".literal)
            }
        }
    }
}