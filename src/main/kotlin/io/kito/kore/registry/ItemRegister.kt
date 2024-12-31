package io.kito.kore.registry

import io.kito.kore.util.itemProp
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty


open class ItemRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.createItems(id)

    infix fun <T : Item> String.of(supplier: (Properties) -> T) = ItemBuilder(this, supplier)
    infix fun <T : Item> String.where(supplier: (Properties) -> T) = ItemBuilder(this, supplier) where {}

    override fun register(bus: IEventBus) = register.register(bus)

    inner class ItemBuilder<T : Item>(val name: String, private val supplier: (Properties) -> T) {

        private val properties = itemProp()

        fun props(block: Properties.() -> Unit) = properties.apply(block)

        infix fun where(builder: ItemBuilder<T>.() -> Unit) =
            apply(builder).let { register.register(name) { -> supplier(properties) } }
    }
}