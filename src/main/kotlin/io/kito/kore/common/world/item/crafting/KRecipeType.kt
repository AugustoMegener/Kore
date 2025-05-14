package io.kito.kore.common.world.item.crafting

import io.kito.kore.common.data.codec.stream.KStreamMapCodecSerializer
import io.kito.kore.common.registry.AutoRegister
import net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER
import net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

open class KRecipeType<T : Recipe<*>>(location: ResourceLocation, clazz: KClass<T>) :
    RecipeSerializer<T>,
    AutoRegister, KStreamMapCodecSerializer<RegistryFriendlyByteBuf, T>(clazz, RegistryFriendlyByteBuf::class)
{
    override val id: String = location.namespace

    private val name = location.path

    val type: RecipeType<T> by lazy { RecipeType.register<T>(name) }

    override fun codec() = mapCodec

    override fun streamCodec() = streamCodec

    override fun register(bus: IEventBus) {
        if (!this::class.hasAnnotation<RegisterRecipeType>()) return

        DeferredRegister.create(RECIPE_TYPE, id).run {
            register("${name}_type") { -> type }
            register(bus)
        }
        DeferredRegister.create(RECIPE_SERIALIZER, id).run {
            register(name) { -> this@KRecipeType }
            register(bus)
        }
    }
}