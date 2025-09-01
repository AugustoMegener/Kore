package io.kito.kore.common.data.nbt


import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

@RegisterNBTSerializable(Component::class)
object ComponentSerializer : NBTStatelessSerializer<Component>() {

    override fun serialize(value: Component, provider: HolderLookup.Provider) =
        CompoundTag().also { it.putString("component", Component.Serializer.toJson(value, provider)) }

    override fun deserialize(provider: HolderLookup.Provider, nbt: CompoundTag): Component =
        Component.Serializer.fromJson(nbt.getString("component"), provider)!!

}