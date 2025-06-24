package io.kito.kore.common.registry

import com.mojang.serialization.Codec
import net.minecraft.nbt.Tag
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries.ATTACHMENT_TYPES

/**
 * A utility class for registering custom [AttachmentType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of attachment types
 * with NeoForge, simplifying the process of adding custom data to various game objects.
 *
 * @property id The mod ID or namespace for these attachment types.
 */
open class AttachmentTypeRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [AttachmentType]s, tied to the given mod ID.
     */
    private val register = DeferredRegister.create(ATTACHMENT_TYPES, id)

    /**
     * Infix function to define a new [AttachmentType] with a default value supplier and a [Codec].
     * This is the first step in a chain to register a codec-serializable attachment type.
     *
     * @param T The type of the data stored in the attachment.
     * @param name The name of the attachment type (e.g., "my_attachment").
     * @param value A lambda that supplies the default value for the attachment.
     * @return A [CodecAttachBuilder] to continue the registration process.
     */
    infix fun <T> String.of(value: () -> T) = CodecAttachBuilder(this, value)

    /**
     * Infix function to define a new [AttachmentType] that caches its value.
     * This is suitable for attachments that don't need complex serialization but should retain their value.
     *
     * @param T The type of the data stored in the attachment.
     * @param name The name of the attachment type.
     * @param value A lambda that supplies the default value for the attachment.
     * @return A [DeferredRegister.DeferredHolder] for the registered [AttachmentType].
     */
    infix fun <T> String.cacheOf(value: () -> T) = register.register(this) { -> AttachmentType.builder(value).build() }

    /**
     * Infix function to define a new [AttachmentType] for data that implements [INBTSerializable].
     * This allows for automatic serialization and deserialization of the attachment data to NBT.
     *
     * @param S The type of the NBT [Tag] used for serialization.
     * @param T The type of the data stored in the attachment, which must implement [INBTSerializable].
     * @param name The name of the attachment type.
     * @param value A lambda that supplies the default value for the attachment.
     * @return A [DeferredRegister.DeferredHolder] for the registered [AttachmentType].
     */
    infix fun <S : Tag, T : INBTSerializable<S>> String.on(value: () -> T) =
        register.register(this) { -> AttachmentType.serializable(value).build() }

    /**
     * Inner class to facilitate the registration of [AttachmentType]s that use a [Codec] for serialization.
     *
     * @param T The type of the data stored in the attachment.
     * @property name The name of the attachment type.
     * @property value A lambda that supplies the default value for the attachment.
     */
    inner class CodecAttachBuilder<T>(val name: String, val value: () -> T) {
        /**
         * Infix function to specify the [Codec] to be used for serializing and deserializing the attachment data.
         *
         * @param codec The [Codec] for the attachment data type.
         * @return A [DeferredRegister.DeferredHolder] for the registered [AttachmentType].
         */
        infix fun with(codec: Codec<T>) =
            register.register(name) { -> AttachmentType.builder(value).serialize(codec).build() }
    }

    /**
     * Registers the [DeferredRegister] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to register all defined attachment types.
     *
     * @param bus The [IEventBus] to register with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) { register.register(bus) }
}

