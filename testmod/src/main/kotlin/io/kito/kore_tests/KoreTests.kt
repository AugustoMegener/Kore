package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.KCodecSerializer
import io.kito.kore.common.data.nbt.KNBTSerializable
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.BlockRegister
import io.kito.kore.common.registry.CreativeModeTabRegister
import io.kito.kore.common.registry.DataComponentTypeRegister
import io.kito.kore.common.world.level.block.KBaseEntityBlock
import io.kito.kore.common.world.level.block.entity.KBlockEntity
import io.kito.kore.util.*
import io.kito.kore_tests.Foo.blockModel
import io.kito.kore_tests.Foo.named
import io.kito.kore_tests.Foo.state
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items.DIAMOND
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.event.level.LevelEvent
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.ItemStackHandler
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@KMod
fun init() {
    val nonNullList = typeOf<NonNullList<*>>()
    val mutableList = typeOf<MutableList<*>>()

    val foo = nonNullList.isSubtypeOf(mutableList) // true (???)
}

@Scan
object Events {
    @KSubscribe
    fun FMLCommonSetupEvent.onLoad() {
        logger.info("$ID loaded!")
    }

    @KSubscribe
    fun LevelEvent.Load.onLoadLevel() {
        logger.info("NBT SERIALIZATION TEST START")


        logger.info("NBT SERIALIZATION TEST END")
    }
}

@Scan
object Foo : DataGenHelper(ID)

@Scan
object Bar : DataComponentTypeRegister(ID) {

    val foo = "foo" of { persistent(CustomData.codec) }
}

@Scan
object Blocks : BlockRegister(ID) {

    val block by "block" of ::CustomBlock where {
        named(EN_US to "Block",
              PT_BR to "Bloco")

        state { loc, block -> simpleBlock(block) }

        props { explosionResistance(1000f) }

        defaultItem {
            blockModel()
            props {
                stacksTo(1)
            }
        }

        blockEntity(::CustomBlockEntity) {
            withCaps {
                blockItemHandler on { inventory }
            }
        }
    }

    val blockItem   by ::block.blockItem
    val blockEntity by ::block.blockEntity
}

@Scan
object Tabs : CreativeModeTabRegister(ID) {
    val myTab by "tab" where {
        icon { Blocks.blockItem.defaultInstance }

        named(it, EN_US to "My Tab",
                  PT_BR to "Minha Aba")

        items(Blocks::blockItem)
    }
}

class CustomBlock(properties: BlockProp) : KBaseEntityBlock<CustomBlockEntity>(properties) {

    override fun useWithoutItem(state     : BlockState,
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
                           hitResult : BlockHitResult): ItemInteractionResult
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

class CustomBlockEntity(pos: BlockPos, blockState: BlockState) : KBlockEntity(pos, blockState) {

    @Save
    var name by AutoDirt("nothing")

    @Save
    val inventory = ItemStackHandler(1)

    init {
        blockPos
    }

}

data class CustomData(@Save val name         : String,
                      @Save val age          : Int,
                      @Save val pronoums     : List<List<String>>,
                      @Save val privetPrivet : Boolean)
{
    @Save
    var idk = "ohohoo loll"

    override fun toString() = """
        Name: $name
        Age: $age
        Pronoums: ${pronoums.joinToString("; ") { it.joinToString(", ") }}
        is ${ if (privetPrivet) "" else "not " }privet privet!
    """.trimIndent()

    companion object : KCodecSerializer<CustomData>(CustomData::class)
}

class CustomNbtData : KNBTSerializable {
    @Save var str = "grooo"
    @Save var int = 345
    @Save val items = stackHandlerOf(4, 0 to DIAMOND.defaultInstance)
    @Save var data = CustomData("kito", 18, listOf(listOf("ele", "dele"), listOf("he", "him")), true)

    @Save var list = NonNullList.of(0, 1, 2, 3)
    @Save val arrayList = arrayListOf(1, 2, 3)
    //@Save var array = arrayOf(1, 2, 3)

    override fun toString() = """
Str: $str
Int: $int
Items: ${(0..<items.slots).joinToString("; ") { items[it].toString() }}
List: $list
Array list: $arrayList
data: 
${data.toString().lines().joinToString("\n") { "-> $it" } }
    """.trimIndent()
}