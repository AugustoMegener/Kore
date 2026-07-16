package io.kito.kore.client.renderer

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.findAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterClientReloadListener(val id: String) {

    @Scan
    companion object {

        @ObjectScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: PreparableReloadListener) {
            val id = data::class.findAnnotation<RegisterClientReloadListener>()?.id ?: return

            container.eventBus?.addListener { event: AddClientReloadListenersEvent ->
                event.addListener(loc(info.modId, id), data)
            }
        }
    }
}

