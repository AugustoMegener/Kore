package io.kito.kore.common.data.nbt

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

sealed interface ValueSerializer<T> {

    fun serialize(input: ValueOutput, value: T)
}