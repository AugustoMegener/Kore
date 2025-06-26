package io.kito.kore.common.registry

import io.kito.kore.common.capabilities.ItemCapRegister
import io.kito.kore.common.capabilities.ItemCapRegister.ItemCapRegistry
import io.kito.kore.util.minecraft.itemProp
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.registries.DeferredItem


/**
 * A utility class for registering custom [Item]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of items
 * with NeoForge, simplifying the process of adding new items to the game.
 *
 * @property id The mod ID or namespace for these items.
 */
open class ItemRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [Item]s, tied to the given mod ID.
     */
    private val register = DeferredRegister.createItems(id)

    /**
     * Infix function to define a new [Item] with a supplier for creating [Item] instances.
     * This is the first step in a chain to register an item.
     *
     * @param T The type of the [Item] to be registered.
     * @param name The name of the item (e.g., "my_item").
     * @param supplier A lambda that supplies a new instance of the [Item] given [Properties].
     * @return An [ItemBuilder] to continue the registration process.
     */
    infix fun <T : Item> String.   of(supplier: (Properties) -> T) = ItemBuilder(this, supplier)
    /**
     * Infix function to define a new [Item] with a supplier for creating [Item] instances, followed by a `where` clause.
     * This is an alternative syntax for starting the item registration chain.
     *
     * @param T The type of the [Item] to be registered.
     * @param name The name of the item (e.g., "my_item").
     * @param supplier A lambda that supplies a new instance of the [Item] given [Properties].
     * @return An [ItemBuilder] to continue the registration process.
     */
    infix fun <T : Item> String.where(supplier: (Properties) -> T) = ItemBuilder(this, supplier) where {}

    /**
     * Registers the [DeferredRegister] with the provided [IEventBus].
     * This method is called by Kore during mod initialization to register all defined items.
     *
     * @param bus The [IEventBus] to register with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) = register.register(bus)

    /**
     * Inner class to facilitate the building and registration of [Item]s.
     *
     * @param T The type of the [Item] that this builder handles.
     * @property name The name of the item.
     * @property supplier A lambda that supplies a new instance of the [Item] given [Properties].
     */
    inner class ItemBuilder<T : Item>(val name: String, private val supplier: (Properties) -> T) {

        val itemName = name

        private val properties = itemProp()

        private val itemCaps = ItemCaps()

        /**
         * Configures the properties of the item.
         * @param block A lambda that takes an [Properties] instance and applies properties to it.
         * @return This [ItemBuilder] for fluent chaining.
         */
        fun props(block: Properties.() -> Unit) = properties.apply(block)

        /**
         * Configures capabilities for this item.
         * @param adder A lambda that takes an [ItemCaps] instance and adds capabilities to it.
         * @return This [ItemBuilder] for fluent chaining.
         */
        fun caps(adder: ItemCaps.() -> Unit) = also { itemCaps.apply(adder) }

        /**
         * Inner class for defining and collecting item capabilities.
         */
        inner class ItemCaps {
            val registries = arrayListOf<ItemCapRegistry<*, *, *>>()

            /**
             * Operator function to add a capability with a context object.
             * @param O The type of the capability object.
             * @param C The type of the context object.
             * @param getter A lambda that takes an [ItemStack] and an optional context object, and returns the capability object.
             */
            operator fun <O, C> ItemCapability<O, C>.invoke(getter: (ItemStack, C?) -> O) {
                registries += ItemCapRegistry(this, getter)

                /**
                 * Operator function to add a capability without a context object (Void?).
                 * @param O The type of the capability object.
                 * @param getter A lambda that takes an [ItemStack] and returns the capability object.
                 */
                operator fun <O> ItemCapability<O, C>.invoke(getter: (ItemStack) -> O) {
                    registries += ItemCapRegistry(this) { it, _ -> getter(it) }
                }
            }
        }

        /**
         * Finalizes the item registration process.
         * This method registers the item with NeoForge.
         *
         * @param builder A lambda that takes this [ItemBuilder] and applies additional configurations.
         * @return A [DeferredItem] for the registered [Item].
         */
        infix fun where(builder: ItemBuilder<T>.() -> Unit): DeferredItem<T> {
            apply(builder)

            val reg = register.register(name) { -> supplier(properties) }

            ItemCapRegister.itemCaps += { reg.value() } to itemCaps.registries

            return reg
        }
    }
}

