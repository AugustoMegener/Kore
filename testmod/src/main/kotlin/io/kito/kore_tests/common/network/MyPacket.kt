package io.kito.kore_tests.common.network

import io.kito.kore.common.data.codec.stream.KStreamCodecSerializer
import io.kito.kore.common.data.codec.stream.Send
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore_tests.KoreTests.local
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import kotlin.reflect.KClass

data class MyPacket(@Send val name: String,
                    @Send val age: Int,
                    @Send val pronouns: List<String>) : Packet(MyPacket)
{
    override fun invoke(ctx: IPayloadContext?) {
        TODO("Not yet implemented")
    }

    @Scan
    companion object : PacketType<RegistryFriendlyByteBuf, MyPacket>(local(""), MyPacket::class) {

        @KSubscribe
        fun RegisterPayloadHandlersEvent.register() {
           with(registrar("1")) {
               playToServer(type, codec) { pkt, ctx ->

               }
           }
        }
    }
}

abstract class PacketType<B :  RegistryFriendlyByteBuf, T : Packet>(id: ResourceLocation, clazz: KClass<T>)
    : CustomPacketPayload, IPayloadHandler<T>, KStreamCodecSerializer<B, T>(clazz)
{
    val type = CustomPacketPayload.Type<PacketType<B, T>>(id)

    override fun type() = type

    override fun handle(data: T, ctx: IPayloadContext) = data(ctx)
}

abstract class Packet(type: PacketType<*>) : CustomPacketPayload by type {

    open var targetPlayer: ServerPlayer? = null

    abstract operator fun invoke(ctx: IPayloadContext? = null)

    fun send() {
        runForDist({ PacketDistributor.sendToServer(this) },
            { if (targetPlayer != null) PacketDistributor.sendToPlayer(targetPlayer!!, this)
            else PacketDistributor.sendToAllPlayers(this) })
    }

    fun sync() { this(); send() }

}