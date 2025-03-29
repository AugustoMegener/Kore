package io.kito.kore.common.resource

import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.hasAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterReloadListener {

    @Scan
    companion object {

        private val reloadListeners = arrayListOf<PreparableReloadListener>()

        @ObjectScanner(PreparableReloadListener::class)
        fun collectScanners(info: IModInfo, container: ModContainer, data: PreparableReloadListener)
            { if (data::class.hasAnnotation<RegisterReloadListener>()) reloadListeners += data }

        @KSubscribe
        fun AddReloadListenerEvent.onAddReloadListener() { reloadListeners.forEach(::addListener) }
    }
}
