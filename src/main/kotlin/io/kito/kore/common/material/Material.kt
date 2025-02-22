package io.kito.kore.common.material

import net.minecraft.resources.ResourceLocation

interface Material<K: MaterialKind<*>> {

    val kind: K
    val id: ResourceLocation

    fun nameFromTemplate(template: String): String
}