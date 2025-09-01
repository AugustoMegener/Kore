package io.kito.kore.common.data.nbt

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag

abstract class NBTStatelessSerializer<T> : NBTSerializer<T> {

    abstract fun deserialize(provider: HolderLookup.Provider, nbt: CompoundTag) : T
}