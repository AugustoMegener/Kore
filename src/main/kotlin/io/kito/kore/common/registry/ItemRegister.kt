package io.kito.kore.common.registry

import io.kito.kore.common.capabilities.ItemCapRegister
import io.kito.kore.common.capabilities.ItemCapRegister.ItemCapRegistry
import io.kito.kore.util.minecraft.ItemProp
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister


open class ItemRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.createItems(id)

    open val dataComponentRegister: DataComponentTypeRegister? = null

    infix fun <T : Item> String.   of(supplier: (Properties) -> T) = ItemBuilder(this, supplier)

    infix fun <T : Item> String.where(supplier: (Properties) -> T) = ItemBuilder(this, supplier) where {}

    override fun register(bus: IEventBus)  {
        dataComponentRegister?.register(bus)
        register.register(bus)
    }

    inner class ItemBuilder<T : Item>(val name: String, private val supplier: (Properties) -> T) {

        val itemId = loc(id, name)

        private var properties: (Properties) -> Properties = { it }

        private val itemCaps = ItemCaps()

        fun props(block: Properties.() -> Properties) {
            val previous = properties
            properties = { previous(it).block() }
        }

        fun caps(adder: ItemCaps.() -> Unit) = also { itemCaps.apply(adder) }

        inner class ItemCaps {
            val registries = arrayListOf<ItemCapRegistry<*, *, *>>()

            operator fun <O, C> ItemCapability<O, C>.invoke(getter: (ItemStack, C?) -> O?) {
                registries += ItemCapRegistry(this, getter)

                operator fun <O> ItemCapability<O, C>.invoke(getter: (ItemStack) -> O?) {
                    registries += ItemCapRegistry(this) { it, _ -> getter(it) }
                }
            }
        }

        infix fun where(builder: ItemBuilder<T>.() -> Unit): DeferredItem<T> {
            apply(builder)

            val reg = register.registerItem(name) {
                supplier(properties(ItemProp().setId(ResourceKey.create(Registries.ITEM, loc(id, name)))))
            }

            ItemCapRegister.itemCaps += reg::value to itemCaps.registries

            return reg
        }
    }
}

