package io.kito.kore.common.registry.early

import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KProperty

abstract class EarlyRegister<T>(val id: String, val registry: EarlyRegistry<T>) {

    infix fun <E : T> String.of(supplier: () -> E): Entry<E> {
        val location = loc(id, this)
        registry.addEntry(location, supplier)

        return Entry(location)
    }

    inner class Entry<E : T>(val location: ResourceLocation) {

        @Suppress(UNCHECKED_CAST)
        val supplier = { registry[location] as E }

        operator fun getValue(obj: Any?, prop: KProperty<*>) = supplier()

        infix fun onGroup(group: EarlyRegistryGroup) = also { registry.groupEntries += location to group }
        infix fun onGroups(groups: Array<EarlyRegistryGroup>) =
            also { groups.forEach { registry.groupEntries += location to it }  }
    }
}