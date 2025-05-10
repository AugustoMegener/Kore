package io.kito.kore.client.renderer

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.hasAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterClientReloadListener {

    @Scan
    companion object {
        @ObjectScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: PreparableReloadListener) {
            if (!data::class.hasAnnotation<RegisterClientReloadListener>()) return

            container.eventBus?.addListener { event: RegisterClientReloadListenersEvent ->
                event.registerReloadListener(data)
            }
        }
    }
}
