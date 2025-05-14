package io.kito.kore.common.data.codec.stream

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import io.kito.kore.common.data.codec.stream.StreamCodecSource.Companion.streamCodec
import io.kito.kore.common.reflect.Scan
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.common.crafting.SizedIngredient
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*
import kotlin.reflect.KType


@Scan
object StreamCodecSources {
    @StreamCodecSource
    fun intCodec(): StreamCodec<ByteBuf, Int> = ByteBufCodecs.INT

    @StreamCodecSource
    fun varIntCodec(): StreamCodec<ByteBuf, Int> = ByteBufCodecs.VAR_INT

    @StreamCodecSource
    fun stringCodec(): StreamCodec<ByteBuf, String> = ByteBufCodecs.STRING_UTF8

    @StreamCodecSource
    fun boolCodec(): StreamCodec<ByteBuf, Boolean> = ByteBufCodecs.BOOL

    @StreamCodecSource
    fun byteCodec(): StreamCodec<ByteBuf, Byte> = ByteBufCodecs.BYTE

    @StreamCodecSource
    fun shortCodec(): StreamCodec<ByteBuf, Short> = ByteBufCodecs.SHORT

    @StreamCodecSource
    fun unsignedShortCodec(): StreamCodec<ByteBuf, Int> = ByteBufCodecs.UNSIGNED_SHORT

    @StreamCodecSource
    fun longCodec(): StreamCodec<ByteBuf, Long> = ByteBufCodecs.VAR_LONG

    @StreamCodecSource
    fun floatCodec(): StreamCodec<ByteBuf, Float> = ByteBufCodecs.FLOAT

    @StreamCodecSource
    fun doubleCodec(): StreamCodec<ByteBuf, Double> = ByteBufCodecs.DOUBLE

    @StreamCodecSource
    fun byteArrayCodec(): StreamCodec<ByteBuf, ByteArray> = ByteBufCodecs.BYTE_ARRAY

    @StreamCodecSource
    fun boundedByteArrayCodec(maxSize: Int): StreamCodec<ByteBuf, ByteArray> =
        ByteBufCodecs.byteArray(maxSize)

    @StreamCodecSource
    fun tagCodec(): StreamCodec<ByteBuf, Tag> = ByteBufCodecs.TAG

    @StreamCodecSource
    fun trustedTagCodec(): StreamCodec<ByteBuf, Tag> = ByteBufCodecs.TRUSTED_TAG

    @StreamCodecSource
    fun compoundTagCodec(): StreamCodec<ByteBuf, CompoundTag> = ByteBufCodecs.COMPOUND_TAG

    @StreamCodecSource
    fun trustedCompoundTagCodec(): StreamCodec<ByteBuf, CompoundTag> = ByteBufCodecs.TRUSTED_COMPOUND_TAG

    @StreamCodecSource
    fun optionalCompoundTagCodec(): StreamCodec<ByteBuf, Optional<CompoundTag>> =
        ByteBufCodecs.OPTIONAL_COMPOUND_TAG

    @StreamCodecSource
    fun vector3fCodec(): StreamCodec<ByteBuf, Vector3f> = ByteBufCodecs.VECTOR3F

    @StreamCodecSource
    fun quaternionfCodec(): StreamCodec<ByteBuf, Quaternionf> = ByteBufCodecs.QUATERNIONF

    @StreamCodecSource
    fun gameProfilePropertiesCodec(): StreamCodec<ByteBuf, PropertyMap> =
        ByteBufCodecs.GAME_PROFILE_PROPERTIES

    @StreamCodecSource
    fun gameProfileCodec(): StreamCodec<ByteBuf, GameProfile> = ByteBufCodecs.GAME_PROFILE

    @StreamCodecSource
    fun listCodec(type: KType) = ByteBufCodecs.collection(::ArrayList, type.arguments[0].type!!.streamCodec)


    @StreamCodecSource
    fun eitherCodec(type: KType) =
        ByteBufCodecs.either(type.arguments[0].type!!.streamCodec, type.arguments[1].type!!.streamCodec)

    @StreamCodecSource
    fun optionalCodec(type: KType) =
        ByteBufCodecs.optional(type.arguments[0].type!!.streamCodec<ByteBuf, Any>())

    @StreamCodecSource
    fun mapCodec(type: KType) =
        ByteBufCodecs.map(
            ::LinkedHashMap,
            type.arguments[0].type!!.streamCodec,
            type.arguments[1].type!!.streamCodec
        )

    @StreamCodecSource
    fun ingredientCodec(): StreamCodec<RegistryFriendlyByteBuf, Ingredient> = Ingredient.CONTENTS_STREAM_CODEC

    @StreamCodecSource
    fun sizedIngredientCodec(): StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> = SizedIngredient.STREAM_CODEC

    @StreamCodecSource
    fun itemStackCodec(): StreamCodec<RegistryFriendlyByteBuf, ItemStack> = ItemStack.STREAM_CODEC
}