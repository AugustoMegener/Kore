package io.kito.kore.common.network

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.findAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterPacket(val version: String, val target: PacketTarget) {

    @Scan
    companion object {
        @ObjectScanner(PacketType::class)
        fun registerScanner(info: IModInfo, container: ModContainer, data: PacketType<*>) {
            val registry = data::class.findAnnotation<RegisterPacket>() ?: return

            container.eventBus?.addListener { it: RegisterPayloadHandlersEvent ->
                val reg = it.registrar(registry.version)

                when (registry.target) {
                    PacketTarget.SERVER ->
                        reg.playToServer(
                            data.type as CustomPacketPayload.Type<Packet>,
                            data.codec as StreamCodec<in RegistryFriendlyByteBuf, Packet>,
                            data as IPayloadHandler<Packet>
                        )
                    PacketTarget.CLIENT ->
                        reg.playToClient(
                            data.type as CustomPacketPayload.Type<Packet>,
                            data.codec as StreamCodec<in RegistryFriendlyByteBuf, Packet>,
                            data as IPayloadHandler<Packet>
                        )
                    PacketTarget.BOTH ->
                        reg.playBidirectional(
                            data.type as CustomPacketPayload.Type<Packet>,
                            data.codec as StreamCodec<in RegistryFriendlyByteBuf, Packet>,
                            data as IPayloadHandler<Packet>
                        )
                }
            }
        }
    }
}
