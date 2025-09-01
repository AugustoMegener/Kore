package io.kito.kore.common.data.nbt

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag

sealed interface NBTSerializer<T> {

    fun serialize(value: T, provider: HolderLookup.Provider): CompoundTag
}