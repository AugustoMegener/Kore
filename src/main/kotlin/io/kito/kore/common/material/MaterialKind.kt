package io.kito.kore.common.material

import net.minecraft.resources.ResourceLocation

abstract class MaterialKind<M: Material<MaterialKind<M>>>(val id: ResourceLocation) {

    private val registeredMaterials = arrayListOf<M>()

    val materials by lazy { listOf(registeredMaterials) }

    fun register(material: M) { registeredMaterials += material }
}
