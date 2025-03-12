package io.kito.kore.util

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.resources.ResourceLocation.withDefaultNamespace
import net.minecraft.resources.ResourceLocation.parse
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.api.distmarker.Dist
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

typealias BlockProp = BlockBehaviour.Properties
typealias  ItemProp = Properties

const val EN_US = "en_us"
const val PT_BR = "pt_br"

fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

fun String.toLoc() = parse(this)

fun itemProp() = Item.Properties()
fun blockProp(): BlockBehaviour.Properties = BlockBehaviour.Properties.of()

fun <T> recordCodecOf(builder: RecordCodecBuilder.Instance<T>.() -> App<RecordCodecBuilder.Mu<T>, T>): Codec<T> =
    RecordCodecBuilder.create { builder(it) }

fun <T> createDynamicCodec(fields: List<App<RecordCodecBuilder.Mu<T>, out Any>>, decoder: (List<Any>) -> T) =
    recordCodecOf {
        when (fields.size) {
             1 ->  group(fields[0]).apply(this) { f0 -> decoder(listOf(f0)); }
             2 ->  group(fields[0], fields[1]).apply(this) { f0, f1 -> decoder(listOf(f0, f1)) }
             3 ->  group(fields[0], fields[1], fields[2]).apply(this) { f0, f1, f2 -> decoder(listOf(f0, f1, f2)) }
             4 ->  group(fields[0], fields[1], fields[2], fields[3]).apply(this) { f0, f1, f2, f3 -> decoder(listOf(f0, f1, f2, f3)) }
             5 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4]).apply(this) { f0, f1, f2, f3, f4 -> decoder(listOf(f0, f1, f2, f3, f4)) }
             6 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]).apply(this) { f0, f1, f2, f3, f4, f5 -> decoder(listOf(f0, f1, f2, f3, f4, f5)) }
             7 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]).apply(this) { f0, f1, f2, f3, f4, f5, f6 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6)) }
             8 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7)) }
             9 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8)) }
            10 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9)) }
            11 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10)) }
            12 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11)) }
            13 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12)) }
            14 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13)) }
            15 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14)) }
            16 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15)) }
            else -> throw IllegalArgumentException("Unsupported number of fields: ${fields.size}")
        }
    }


inline val   nbtOps:  NbtOps get() =  NbtOps.INSTANCE
inline val  jsonOps: JsonOps get() = JsonOps.INSTANCE
inline val jsoncOps: JsonOps get() = JsonOps.COMPRESSED

operator fun CompoundTag.set(name: String, tag: Tag) = put(name, tag)

val minecraftClient: Minecraft get() = runForDist(
    { Minecraft.getInstance() },
    { throw IllegalStateException("trying to get the minecraft client on the logical server") })

fun withMcClient(run: (Minecraft) -> Unit) {
    runWhenOn(Dist.CLIENT) { run(minecraftClient) }
}

inline val String.literal: MutableComponent get() = Component.literal(this)