package io.kito.kore_tests.client

import com.mojang.blaze3d.platform.InputConstants.KEY_J
import io.kito.kore.client.InputRegistry
import io.kito.kore.client.RegisterInput
import io.kito.kore.common.data.codec.stream.Send
import io.kito.kore.common.network.Packet
import io.kito.kore.common.network.PacketTarget
import io.kito.kore.common.network.PacketType
import io.kito.kore.common.network.RegisterPacket
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.keySysMain
import io.kito.kore.util.minecraft.literal
import io.kito.kore_tests.KoreTests.keyCategoryLocale
import io.kito.kore_tests.KoreTests.keyLocale
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.Config.inputMsg
import io.kito.kore_tests.common.network.PACKETS_VERSION
import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.network.handling.IPayloadContext

@Scan
object KeyMappings {

    @RegisterInput
    val myInputKeyMapping by InputRegistry {
        KeyMapping(keyLocale("my_input"),
                   keySysMain, KEY_J,
                   keyCategoryLocale("my_inputs"))
    } sends ::MyInputPacket

    data class MyInputPacket(@Send var bool: Boolean) : Packet(MyInputPacket) {

        constructor() : this(true)

        override fun invoke(ctx: IPayloadContext?) {
            ctx.main {
                ctx!!.player().sendSystemMessage("Key pressed! $inputMsg".literal)
            }
        }

        @RegisterPacket(PACKETS_VERSION, PacketTarget.SERVER)
        companion object : PacketType<MyInputPacket>(local("my_input"), MyInputPacket::class)
    }
}