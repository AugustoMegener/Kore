package io.kito.kore.server.command

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask
import java.util.function.Consumer

abstract class IKConfigurationTask(private val listener: ServerConfigurationPacketListener, location: ResourceLocation)
    : ICustomConfigurationTask
{
    private val type = ConfigurationTask.Type(location)

    override fun run(sender: Consumer<CustomPacketPayload?>) {
        sender.accept(getPayload())
        listener.finishCurrentTask(type)
    }

    override fun type() = type

    abstract fun getPayload(): CustomPacketPayload
}