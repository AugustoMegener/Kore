package io.kito.kore.common.data.codec

import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.Dynamic
import com.mojang.serialization.codecs.PrimitiveCodec
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.common.reflect.Scan
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.common.crafting.SizedIngredient
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream
import kotlin.reflect.KType


@Scan
object CodecSources {
    @CodecSource
    fun intCodec(): PrimitiveCodec<Int> = Codec.INT

    @CodecSource
    fun stringCodec(): PrimitiveCodec<String> = Codec.STRING

    @CodecSource
    fun boolCodec(): PrimitiveCodec<Boolean> = Codec.BOOL

    @CodecSource
    fun byteCodec(): PrimitiveCodec<Byte> = Codec.BYTE

    @CodecSource
    fun shortCodec(): PrimitiveCodec<Short> = Codec.SHORT

    @CodecSource
    fun longCodec(): PrimitiveCodec<Long> = Codec.LONG

    @CodecSource
    fun floatCodec(): PrimitiveCodec<Float> = Codec.FLOAT

    @CodecSource
    fun doubleCodec(): PrimitiveCodec<Double> = Codec.DOUBLE

    @CodecSource
    fun byteBufferCodec(): PrimitiveCodec<ByteBuffer> = Codec.BYTE_BUFFER

    @CodecSource
    fun intStreamCodec(): PrimitiveCodec<IntStream> = Codec.INT_STREAM

    @CodecSource
    fun longStreamCodec(): PrimitiveCodec<LongStream> = Codec.LONG_STREAM

    @CodecSource
    fun listCodec(type: KType) = Codec.list(type.arguments[0].type!!.codec)

    @CodecSource
    fun pairCodec(type: KType) = Codec.pair(type.arguments[0].type!!.codec, type.arguments[1].type!!.codec)

    @CodecSource
    fun eitherCodec(type: KType) = Codec.either(type.arguments[0].type!!.codec, type.arguments[1].type!!.codec)


    @CodecSource
    fun dynamicCodec(): Codec<Dynamic<*>> = Codec.PASSTHROUGH

    @CodecSource
    fun unitCodec(): Codec<Unit> = Codec.EMPTY.codec()

    @CodecSource
    fun ingredientCodec(): Codec<Ingredient> = Ingredient.CODEC

    @CodecSource
    fun sizedIngredientCodec(): Codec<SizedIngredient> = SizedIngredient.FLAT_CODEC

    @CodecSource
    fun itemStackCodec(): Codec<ItemStack> = ItemStack.CODEC
}