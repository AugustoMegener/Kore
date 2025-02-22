package io.kito.kore.common.capabilities

import com.google.common.collect.ImmutableMap
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.ClassScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.withCapabilityOn
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries.BLOCK
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforgespi.language.IModInfo
import java.util.ArrayList
import kotlin.reflect.KClass

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


class BlockCap<O, C : Any?, T : BlockCapability<O, C>>(val cap: T, val getter: IBlockCapabilityProvider<O, C>) {

    operator fun getValue(obj: Block, prop: KProperty<*>) = ValueGetter(obj)

    inner class ValueGetter internal constructor(val block: Block) {
        operator fun invoke(level: Level, pos: BlockPos, ctx: C? = null) =
            ctx?.let { level.getCapability(cap, pos, it) } ?: level.getCapability(cap as BlockCapability<O, Void?>, pos)
    }

    @Scan
    companion object {

        private val blockCaps by lazy {
            hashMapOf<Block, ArrayList<BlockCap<Any, Any, out BlockCapability<Any, Any>>>>().apply {
                BLOCK.forEach { blck ->
                    blck::class.memberProperties.forEach { fld -> fld as KProperty1<Block, Any>
                        fld.isAccessible = true
                        fld.getDelegate(blck)?.takeIf { it is BlockCap<*, *, *> }?.let { 
                            computeIfAbsent(blck) { arrayListOf() } +=
                                it as BlockCap<Any, Any, out BlockCapability<Any, Any>>
                        }
                    }
                }
            }
        }

        @KSubscribe
        fun RegisterCapabilitiesEvent.onRegisterCaps() {
            blockCaps.forEach { (blck, cap) -> for (it in cap) registerBlock(it.cap, it.getter, blck) }
        }
    }
}