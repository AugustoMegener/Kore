package io.kito.kore.server.network.config

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.findAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterConfigurationTask {

    @Scan
    companion object {

        @ObjectScanner(ConfigurationTask::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: ConfigurationTask) {
            data::class.findAnnotation<RegisterConfigurationTask>() ?: return

            container.eventBus?.addListener { event: RegisterConfigurationTasksEvent ->
                event.register(data)
            }
        }
    }
}