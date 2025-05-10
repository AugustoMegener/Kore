package io.kito.kore.common.world.item.crafting

import io.kito.kore.common.data.codec.KMapCodecSerializer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import kotlin.reflect.KClass

class KRecipeSerializer<T : Recipe<*>>(clazz: KClass<T>) :
    RecipeSerializer<T>, KMapCodecSerializer<T>(clazz)
{
    override fun codec() = mapCodec

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, T?> = TODO()
}