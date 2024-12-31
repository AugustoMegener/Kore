package io.kito.kore.registry

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries.ATTACHMENT_TYPES

open class AttachmentTypeRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.create(ATTACHMENT_TYPES, id)

    infix fun <T> String.of(value: () -> T) = CodecAttachBuilder(this, value)

    infix fun <T> String.cacheOf(value: () -> T) = register.register(this) { -> AttachmentType.builder(value).build() }

    infix fun <S : Tag, T : INBTSerializable<S>> String.on(value: () -> T) =
        register.register(this) { -> AttachmentType.serializable(value).build() }

    inner class CodecAttachBuilder<T>(val name: String, val value: () -> T) {
        infix fun with(codec: Codec<T>) =
            register.register(name) { -> AttachmentType.builder(value).serialize(codec).build() }
    }

    override fun register(bus: IEventBus) { register.register(bus) }
}