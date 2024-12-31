package io.kito.kore.util

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.resources.ResourceLocation.withDefaultNamespace
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockBehaviour

const val EN_US = "en_us"
const val PT_BR = "pt_br"

fun loc(name: String)             : ResourceLocation = withDefaultNamespace(    name)
fun loc(id: String, name: String) : ResourceLocation = fromNamespaceAndPath(id, name)

fun itemProp() = Item.Properties()
fun blockProp(): BlockBehaviour.Properties = BlockBehaviour.Properties.of()

fun <T> createDynamicCodec(fields: List<App<RecordCodecBuilder.Mu<T>, out Any>>, decoder: (List<Any>) -> T) =
    when (fields.size) {
         1 -> RecordCodecBuilder.create<T> { it.group(fields[0]).apply(it) { f0 -> decoder(listOf(f0)); } }
         2 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1]).apply(it) { f0, f1 -> decoder(listOf(f0, f1)) } }
         3 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2]).apply(it) { f0, f1, f2 -> decoder(listOf(f0, f1, f2)) } }
         4 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3]).apply(it) { f0, f1, f2, f3 -> decoder(listOf(f0, f1, f2, f3)) } }
         5 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4]).apply(it) { f0, f1, f2, f3, f4 -> decoder(listOf(f0, f1, f2, f3, f4)) } }
         6 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]).apply(it) { f0, f1, f2, f3, f4, f5 -> decoder(listOf(f0, f1, f2, f3, f4, f5)) } }
         7 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]).apply(it) { f0, f1, f2, f3, f4, f5, f6 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6)) } }
         8 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7)) } }
         9 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8)) } }
        10 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9)) } }
        11 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10)) } }
        12 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11)) } }
        13 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12)) } }
        14 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13)) } }
        15 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14)) } }
        16 -> RecordCodecBuilder.create<T> { it.group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]).apply(it) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15)) } }
        else -> throw IllegalArgumentException("Unsupported number of fields: ${fields.size}")
    }

