package io.kito.kore.common.registry

import io.kito.kore.common.capabilities.ItemCapRegister
import io.kito.kore.common.capabilities.ItemCapRegister.ItemCapRegistry
import io.kito.kore.util.itemProp
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


open class ItemRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.createItems(id)

    infix fun <T : Item> String.   of(supplier: (Properties) -> T) = ItemBuilder(this, supplier)
    infix fun <T : Item> String.where(supplier: (Properties) -> T) = ItemBuilder(this, supplier) where {}

    override fun register(bus: IEventBus) = register.register(bus)

    inner class ItemBuilder<T : Item>(val name: String, private val supplier: (Properties) -> T) {

        val itemName = name

        private val properties = itemProp()

        private val itemCaps = ItemCaps()

        fun props(block: Properties.() -> Unit) = properties.apply(block)

        fun caps(adder: ItemCaps.() -> Unit) = also { itemCaps.apply(adder) }

        inner class ItemCaps {
            val registries = arrayListOf<ItemCapRegistry<*, *, *>>()

            operator fun <O, C> ItemCapability<O, C>.invoke(getter: (ItemStack, C?) -> O) {
                registries += ItemCapRegistry(this, getter)

                operator fun <O> ItemCapability<O, C>.invoke(getter: (ItemStack) -> O) {
                    registries += ItemCapRegistry(this) { it, _ -> getter(it) }
                }
            }
        }

        infix fun where(builder: ItemBuilder<T>.() -> Unit): DeferredItem<T> {
            apply(builder)

            val reg = register.register(name) { -> supplier(properties) }

            ItemCapRegister.itemCaps += { reg.value() } to itemCaps.registries

            return reg
        }
    }
}