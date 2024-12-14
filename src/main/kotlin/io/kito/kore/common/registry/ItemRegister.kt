package io.kito.kore.common.registry

import io.kito.kore.util.itemProp
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister


open class ItemRegister(final override val id: String) : AutoRegister  {

    private val register = DeferredRegister.createItems(id)

    infix fun <T : Item> String.of(supplier: (Properties) -> T): DeferredItem<T> =
        register.register(this) { -> supplier(itemProp()) }

    operator fun <T : Item> String.invoke(supplier: (Properties) -> T) = ItemBuilder(this, supplier)

    override fun register(bus: IEventBus) = register.register(bus)

    inner class ItemBuilder<T : Item>(private val name: String, private val supplier: (Properties) -> T) {

        infix fun where(builder: ItemBuilder<T>.() -> Unit) = (name of supplier).also { apply(builder) }
    }
}