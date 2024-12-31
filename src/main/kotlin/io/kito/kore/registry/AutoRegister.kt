package io.kito.kore.registry

import io.kito.kore.reflect.ObjectScanner
import io.kito.kore.reflect.Scan
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo

interface AutoRegister {

    val id: String

    fun register(bus: IEventBus)

    @Scan
    companion object {
        @ObjectScanner(AutoRegister::class)
        fun registerKoreAutoRegistries(info: IModInfo, container: ModContainer, data: AutoRegister) {
            data.register(container.eventBus ?: return)
        }
    }
}