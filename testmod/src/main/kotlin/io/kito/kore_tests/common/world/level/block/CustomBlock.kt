package io.kito.kore_tests.common.world.level.block

import io.kito.kore.common.world.level.block.KBaseEntityBlock
import io.kito.kore.util.minecraft.BlockProp
import io.kito.kore_tests.common.world.level.block.entity.CustomBlockEntity

class CustomBlock(properties: BlockProp) : KBaseEntityBlock<CustomBlockEntity>(properties) {

    /*override fun useWithoutItem(state     : BlockState,
                                level     : Level,
                                pos       : BlockPos,
                                player    : Player,
                                hitResult : BlockHitResult): InteractionResult
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
    }*/
}