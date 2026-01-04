package com.cosmic_jewelry.common.core.preset

import io.kito.kore.Kore.ID
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.registry.ItemRegister.ItemBuilder
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture
import net.minecraft.server.packs.PackType
import net.minecraft.world.item.Item

open class ItemPreset<V, T : Item>(val preset: ItemBuilder<T>.(V) -> Unit,
                                   parents: Array<out Preset<V, ItemBuilder<T>>> = arrayOf()) :
    Preset<V, ItemBuilder<T>>(parents)
{
    override fun ItemBuilder<T>.action(value: V) { preset(value) }

    companion object {
        fun <T, I : Item> DataGenHelper.optionalDefaultModelPreset() = ItemPreset<T, I>({ defaultModel() })
    }
}