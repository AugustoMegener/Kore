package io.kito.kore.common.data.codec

import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.Dynamic
import com.mojang.serialization.codecs.PrimitiveCodec
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.common.reflect.Scan
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.neoforged.neoforge.common.crafting.SizedIngredient
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream
import kotlin.reflect.KType

/**
 * A collection of common [Codec] sources for various data types.
 * This object is annotated with `@Scan` to be automatically discovered by Kore,
 * allowing these codecs to be used for serialization and deserialization throughout the mod.
 */
@Scan
object CodecSources {
    /**
     * Provides a [Codec] for `Int` primitive type.
     */
    @CodecSource
    fun intCodec(): PrimitiveCodec<Int> = Codec.INT

    /**
     * Provides a [Codec] for `String` primitive type.
     */
    @CodecSource
    fun stringCodec(): PrimitiveCodec<String> = Codec.STRING

    /**
     * Provides a [Codec] for `Boolean` primitive type.
     */
    @CodecSource
    fun boolCodec(): PrimitiveCodec<Boolean> = Codec.BOOL

    /**
     * Provides a [Codec] for `Byte` primitive type.
     */
    @CodecSource
    fun byteCodec(): PrimitiveCodec<Byte> = Codec.BYTE

    /**
     * Provides a [Codec] for `Short` primitive type.
     */
    @CodecSource
    fun shortCodec(): PrimitiveCodec<Short> = Codec.SHORT

    /**
     * Provides a [Codec] for `Long` primitive type.
     */
    @CodecSource
    fun longCodec(): PrimitiveCodec<Long> = Codec.LONG

    /**
     * Provides a [Codec] for `Float` primitive type.
     */
    @CodecSource
    fun floatCodec(): PrimitiveCodec<Float> = Codec.FLOAT

    /**
     * Provides a [Codec] for `Double` primitive type.
     */
    @CodecSource
    fun doubleCodec(): PrimitiveCodec<Double> = Codec.DOUBLE

    /**
     * Provides a [Codec] for `ByteBuffer`.
     */
    @CodecSource
    fun byteBufferCodec(): PrimitiveCodec<ByteBuffer> = Codec.BYTE_BUFFER

    /**
     * Provides a [Codec] for `IntStream`.
     */
    @CodecSource
    fun intStreamCodec(): PrimitiveCodec<IntStream> = Codec.INT_STREAM

    /**
     * Provides a [Codec] for `LongStream`.
     */
    @CodecSource
    fun longStreamCodec(): PrimitiveCodec<LongStream> = Codec.LONG_STREAM

    /**
     * Provides a [Codec] for `List` types.
     * This is a generic codec that takes the [KType] of the list and retrieves the codec for its elements.
     * @param type The [KType] of the list, e.g., `typeOf<List<String>>()`.
     */
    @CodecSource
    fun listCodec(type: KType) = Codec.list(type.arguments[0].type!!.codec)

    /**
     * Provides a [Codec] for `Pair` types.
     * This is a generic codec that takes the [KType] of the pair and retrieves the codecs for its first and second elements.
     * @param type The [KType] of the pair, e.g., `typeOf<Pair<String, Int>>()`.
     */
    @CodecSource
    fun pairCodec(type: KType) = Codec.pair(type.arguments[0].type!!.codec, type.arguments[1].type!!.codec)

    /**
     * Provides a [Codec] for `Either` types.
     * This is a generic codec that takes the [KType] of the either and retrieves the codecs for its left and right elements.
     * @param type The [KType] of the either, e.g., `typeOf<Either<String, Int>>()`.
     */
    @CodecSource
    fun eitherCodec(type: KType) = Codec.either(type.arguments[0].type!!.codec, type.arguments[1].type!!.codec)

    /**
     * Provides a passthrough [Codec] for `Dynamic` objects.
     * This codec does not perform any serialization/deserialization and simply passes the dynamic data through.
     */
    @CodecSource
    fun dynamicCodec(): Codec<Dynamic<*>> = Codec.PASSTHROUGH

    /**
     * Provides an empty [Codec] for `Unit` type.
     * This codec is used for types that have no data to serialize or deserialize.
     */
    @CodecSource
    fun unitCodec(): Codec<Unit> = Codec.EMPTY.codec()

    /**
     * Provides a [Codec] for Minecraft's `Ingredient` class.
     */
    @CodecSource
    fun ingredientCodec(): Codec<Ingredient> = Ingredient.CODEC

    /**
     * Provides a [Codec] for NeoForge's `SizedIngredient` class.
     */
    @CodecSource
    fun sizedIngredientCodec(): Codec<SizedIngredient> = SizedIngredient.FLAT_CODEC

    /**
     * Provides a [Codec] for Minecraft's `ItemStack` class.
     */
    @CodecSource
    fun itemStackCodec(): Codec<ItemStack> = ItemStack.CODEC

    @CodecSource
    fun placedFeatureSetCodec(): Codec<HolderSet<PlacedFeature>> = PlacedFeature.LIST_CODEC

    @CodecSource
    fun biomeTagKeyCodec(): Codec<TagKey<Biome>> = TagKey.codec(Registries.BIOME)

    @CodecSource
    fun decorationCodec(): Codec<GenerationStep.Decoration> = GenerationStep.Decoration.CODEC
}

