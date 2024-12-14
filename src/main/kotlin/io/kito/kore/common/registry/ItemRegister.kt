package io.kito.kore.common.registry

import io.kito.kore.util.itemProp
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty


open class ItemRegister(final override val id: String) : AutoRegister  {

    private val register = DeferredRegister.createItems(id)

    infix fun <T : Item> String.of(supplier: (Properties) -> T) = ItemBuilder(this, supplier)

    override fun register(bus: IEventBus) = register.register(bus)

    inner class ItemBuilder<T : Item>(private val name: String, private val supplier: (Properties) -> T) {

        private val properties = itemProp()

        fun props(block: Properties.() -> Unit) = properties.apply(block)

        infix fun where(builder: ItemBuilder<T>.() -> Unit) =
            apply(builder).let { ItemRegistry(supplier(properties), name) }
    }

    inner class ItemRegistry<T : Item>(item: T, name: String) {
        private val registry = register.register(name) { -> item }

        operator fun getValue(obj: Any,     property: KProperty<*>) : T = registry.value()
        operator fun getValue(obj: Nothing, property: KProperty<*>) : T = registry.value()

        val key get() = registry.key
    }

    companion object {
        fun <T : Item> isSimple(): ItemBuilder<T>.() -> Unit = {}
    }
}