package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.data.codec.KCodecSerializable
import io.kito.kore.data.codec.Save
import io.kito.kore.datagen.DataGenHelper
import io.kito.kore.event.KSubscribe
import io.kito.kore.reflect.Scan
import io.kito.kore.registry.BlockRegister
import io.kito.kore.registry.CreativeModeTabRegister
import io.kito.kore.registry.DataComponentTypeRegister
import io.kito.kore.util.EN_US
import io.kito.kore.util.PT_BR
import io.kito.kore_tests.Blocks.blockItem
import io.kito.kore_tests.Foo.blockModel
import io.kito.kore_tests.Foo.named
import io.kito.kore_tests.Foo.state
import net.minecraft.core.BlockPos
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

@KMod
fun init() {

}

@Scan
object Events {
    @KSubscribe
    fun FMLCommonSetupEvent.onLoad() {
        logger.info("$ID loaded!")

        logger.info("CODEC SERIALIZATION TEST START")

        val data = CustomData2("kito", 18, listOf(listOf("he", "him")), false)

        val item = blockItem.defaultInstance

        item[Bar.foo] = data
    }
}

@Scan
object Foo : DataGenHelper(ID)

@Scan
object Bar : DataComponentTypeRegister(ID) {

    val foo = "foo" of { persistent(CustomData2.codec) }
}

@Scan
object Blocks : BlockRegister(ID) {

    val block by "block" of ::RotatedPillarBlock where {
        named(EN_US to "Block",
              PT_BR to "Bloco")

        state { _, block -> logBlock(block) }

        props { explosionResistance(1000f) }

        item<BlockItem> {
            blockModel()
            props {
                stacksTo(1)
            }
        }

        blockEntity(::KBlockEntity)
    }

    val blockItem   by ::block.blockItem
    val blockEntity by ::block.blockEntity
}

@Scan
object Tabs : CreativeModeTabRegister(ID) {
    val myTab by "tab" where {
        icon { blockItem.defaultInstance }
        named("tab",
            EN_US to "My Tab",
            PT_BR to "Minha Aba")

        items(
            blockItem
        )
    }
}

class KBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(Blocks.blockEntity, pos, blockState) {

    override fun onLoad() { super.onLoad()
        logger.info("BE loaded!")
    }
}

data class CustomData2(@Save val name: String,
                       @Save val age: Int,
                       @Save val pronoums: List<List<String>>,
                       @Save val privetPrivet: Boolean)
{
    @Save var idk = "ohohoo loll"

    companion object : KCodecSerializable<CustomData2>(CustomData2::class)
}