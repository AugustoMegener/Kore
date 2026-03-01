package io.kito.kore.common.preset

import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import net.minecraft.world.item.Item

open class ItemPreset<V, T : Item>(val preset: ItemBuilder<T>.(V) -> Unit,
                                   parents: Array<out Preset<V, ItemBuilder<T>>> = arrayOf()) :
    Preset<V, ItemBuilder<T>>(parents)
{
    override fun ItemBuilder<T>.action(value: V) { preset(value) }

    companion object {
        fun <T, I : Item> DataGenHelper.optionalDefaultModel() = ItemPreset<T, I>({ defaultModel() })
    }
}