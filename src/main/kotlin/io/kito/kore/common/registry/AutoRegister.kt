package io.kito.kore.common.registry

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo

interface AutoRegister {

    val id: String

    fun register(bus: IEventBus)

    abstract class RegistryTemplate<I, T> {

        private val entries = hashMapOf<I, () -> T>()

        operator fun get(idx: I) = entries[idx]?.invoke()

        fun register(vararg idxs: I) { entries += idxs.associateWith { newEntry(it) } }

        abstract fun newEntry(idx: I): () -> T
    }

    @Scan
    companion object {
        @ObjectScanner(AutoRegister::class)
        fun registerKoreAutoRegistries(info: IModInfo, container: ModContainer, data: AutoRegister) {
            data.register(container.eventBus ?: return)
        }
    }
}