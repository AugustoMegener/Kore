package io.kito.kore.client

import io.kito.kore.common.network.Packet
import net.minecraft.client.KeyMapping
import kotlin.reflect.KProperty

typealias InputAction = () -> Unit
typealias InputPacketSupplier = () -> Packet

/**
 * A registry for managing and associating key mappings with specific actions or packet sending.
 * This class provides a fluent API for defining how an input (key press) should behave.
 *
 * @param keyMappingSupplier A function that supplies the [KeyMapping] instance for this input.
 */
class InputRegistry(keyMappingSupplier: () -> KeyMapping) {

    private val keyMapping by lazy(keyMappingSupplier)

    var canUse: () -> Boolean = { true }; private set
    var action: InputAction = {}; private set
    

    infix fun runs(block: InputAction) = also { action = block }

    infix fun sends(packetSupplier: InputPacketSupplier) =
        also { action = { packetSupplier().send() } }

    infix fun syncs(packetSupplier: InputPacketSupplier) =
        also { action = { packetSupplier().sync() } }

    infix fun justWhen(block: () -> Boolean) =
        also { canUse = block }


    operator fun getValue(cls: Any?, prop: KProperty<*>) = keyMapping
}

