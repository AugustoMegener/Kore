package io.kito.kore.common.network

import io.kito.kore.common.data.codec.stream.KStreamCodecSerializer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import kotlin.reflect.KClass

abstract class PacketType<T : Packet>(id: ResourceLocation, clazz: KClass<T>)
    : CustomPacketPayload, IPayloadHandler<T>, KStreamCodecSerializer<RegistryFriendlyByteBuf, T>(clazz, RegistryFriendlyByteBuf::class)
{
    val type = CustomPacketPayload.Type<T>(id)

    override fun type() = type

    override fun handle(data: T, ctx: IPayloadContext) = data(ctx)
}