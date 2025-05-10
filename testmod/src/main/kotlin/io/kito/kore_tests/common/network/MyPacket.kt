package io.kito.kore_tests.common.network

import io.kito.kore.common.data.codec.stream.Send
import io.kito.kore.common.network.Packet
import io.kito.kore.common.network.PacketTarget
import io.kito.kore.common.network.PacketType
import io.kito.kore.common.network.RegisterPacket
import io.kito.kore_tests.KoreTests.local
import net.neoforged.neoforge.network.handling.IPayloadContext

const val PACKETS_VERSION = "1"

data class MyPacket(@Send val name: String,
                    @Send val age: Int,
                    @Send val pronouns: ArrayList<String>) : Packet(MyPacket)
{
    override fun invoke(ctx: IPayloadContext?) {

    }

    @RegisterPacket(PACKETS_VERSION, PacketTarget.SERVER)
    companion object : PacketType<MyPacket>(local("my_packet"), MyPacket::class)
}