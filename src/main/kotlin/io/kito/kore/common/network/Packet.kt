package io.kito.kore.common.network

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.concurrent.CompletableFuture

abstract class Packet(val type: PacketType<*>) : CustomPacketPayload by type {

    open var targetPlayer: ServerPlayer? = null

    abstract operator fun invoke(ctx: IPayloadContext? = null)

    fun send() {
        runForDist(
            { ClientPacketDistributor.sendToServer(this) },
            {
                if (targetPlayer != null) PacketDistributor.sendToPlayer(targetPlayer!!, this)
                else PacketDistributor.sendToAllPlayers(this)
            })
    }

    fun sync() { this(); send() }

    fun IPayloadContext?.main(block: () -> Unit): CompletableFuture<Void?>? {
        if (this == null) {
            block()
            return null
        }
        else return enqueueWork(block)
    }

    override fun toVanillaClientbound(): ClientboundCustomPayloadPacket? = type.toVanillaClientbound()


    override fun toVanillaServerbound(): ServerboundCustomPayloadPacket? = type.toVanillaServerbound()

}