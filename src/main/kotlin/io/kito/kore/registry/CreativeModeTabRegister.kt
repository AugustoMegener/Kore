package io.kito.kore.registry

import net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

open class CreativeModeTabRegister(final override val id: String) : AutoRegister {

    private val register: DeferredRegister<CreativeModeTab> = DeferredRegister.create(CREATIVE_MODE_TAB, id)

    infix fun String.where(builder: Builder.() -> Unit) = register.register(this, builder().apply(builder)::build)

    fun Builder.items(builder: Output.(ItemDisplayParameters) -> Unit) =
        also { displayItems { p, o -> builder(o, p) } }

    fun Builder.items(vararg items: ItemStack) = items { acceptAll(items.asList()) }

    fun Builder.items(vararg items: ItemLike) = items { acceptAll(items.asList().map { it.asItem().defaultInstance }) }

    override fun register(bus: IEventBus) {
        register.register(bus)
    }
}