package io.kito.kore.common.registry

import net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.*
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

open class CreativeModeTabRegister(final override val id: String) : AutoRegister {

    private val register: DeferredRegister<CreativeModeTab> = DeferredRegister.create(CREATIVE_MODE_TAB, id)

    infix fun String.where(builder: Builder.(String) -> Unit): DeferredHolder<CreativeModeTab, CreativeModeTab> =
        register.register(this, builder().apply { builder(this, this@where) } ::build)

    fun Builder.display(builder: Output.(ItemDisplayParameters) -> Unit) =
        also { displayItems { p, o -> builder(o, p) } }

    fun Output.stacks(vararg stacks: () -> ItemStack) = acceptAll(stacks.asList().map { it() })

    fun Output.items(vararg items: () -> ItemLike) =
        acceptAll(items.asList().map { it().asItem().defaultInstance })

    fun <T> Output.templates(vararg templates: BlockRegister.BlockTemplate<T, *, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.item[i]!! } } }.toTypedArray())

    fun <T> Output.templates(vararg templates: EntityTypeRegister.EntityTypeTemplate<T, *, *>) =
        items(*templates.flatMap { it.allIdxs.map { i -> { it.egg[i]!! } } }.toTypedArray())

    fun <T> Output.templates(vararg templates: RegistryTemplate<T, out Item>) =
        items(*templates.flatMap { it.registereds }.toTypedArray())

    override fun register(bus: IEventBus) {
        register.register(bus)
    }
}