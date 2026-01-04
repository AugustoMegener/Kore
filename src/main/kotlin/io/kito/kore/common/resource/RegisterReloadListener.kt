package io.kito.kore.common.resource

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.ClassScanner
import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent
import net.neoforged.neoforge.event.AddServerReloadListenersEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterReloadListener(val id: String) {

    @Scan
    companion object {

        private val reloadListeners = arrayListOf<Pair<ResourceLocation, () -> PreparableReloadListener>>()

        @ObjectScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: PreparableReloadListener){
            val loc = data::class.findAnnotation<RegisterReloadListener>()?.id?.toLoc() ?: return

            reloadListeners += loc to { data }
        }

        @ClassScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: KClass<PreparableReloadListener>) {
            if (data.objectInstance != null) return
            val loc = data::class.findAnnotation<RegisterReloadListener>()?.id?.toLoc() ?: return

            reloadListeners += loc to { data.primaryConstructor!!.call() }
        }

        @KSubscribe
        fun AddServerReloadListenersEvent.onAddReloadListener()
            { reloadListeners.forEach { (id, lis) -> addListener(id, lis())  } }

        fun <T : PreparableReloadListener> getListener(location: ResourceLocation) =
            reloadListeners.toMap()[location] as T
    }
}
