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

    /**
     * Lazily initialized [KeyMapping] instance associated with this input registry.
     */
    private val keyMapping by lazy(keyMappingSupplier)

    /**
     * A function that determines if the associated action can be used. Defaults to always true.
     */
    var canUse: () -> Boolean = { true }; private set

    /**
     * The action to be performed when the input is triggered.
     */
    var action: InputAction = {}; private set

    /**
     * Specifies the action to be executed when the input is triggered.
     * @param block The [InputAction] to run.
     * @return This [InputRegistry] instance for fluent chaining.
     */
    infix fun runs(block: InputAction) = also { action = block }

    /**
     * Specifies that a packet should be sent when the input is triggered.
     * The packet is sent to the server.
     * @param packetSupplier A function that supplies the [Packet] to send.
     * @return This [InputRegistry] instance for fluent chaining.
     */
    infix fun sends(packetSupplier: InputPacketSupplier) =
        also { action = { packetSupplier().send() } }

    /**
     * Specifies that a packet should be synchronized when the input is triggered.
     * The packet is sent to all relevant clients.
     * @param packetSupplier A function that supplies the [Packet] to synchronize.
     * @return This [InputRegistry] instance for fluent chaining.
     */
    infix fun syncs(packetSupplier: InputPacketSupplier) =
        also { action = { packetSupplier().sync() } }

    /**
     * Specifies a condition under which the input action can be performed.
     * @param block A function that returns `true` if the action can be used, `false` otherwise.
     * @return This [InputRegistry] instance for fluent chaining.
     */
    infix fun justWhen(block: () -> Boolean) = also { canUse = block }

    /**
     * Allows this [InputRegistry] to be used as a delegated property for a [KeyMapping].
     * @param cls The class containing the delegated property.
     * @param prop The property itself.
     * @return The [KeyMapping] associated with this registry.
     */
    operator fun getValue(cls: Any?, prop: KProperty<*>) = keyMapping
}

