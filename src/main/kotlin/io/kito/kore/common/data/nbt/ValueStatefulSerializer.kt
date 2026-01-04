package io.kito.kore.common.data.nbt

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.storage.ValueInput

abstract class ValueStatefulSerializer<T> : ValueSerializer<T> {

     abstract fun deserialize(input: ValueInput, value: T)
 }
