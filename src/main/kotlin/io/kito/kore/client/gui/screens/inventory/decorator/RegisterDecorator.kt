package io.kito.kore.client.gui.screens.inventory.decorator

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.minecraft.client.gui.LayeredDraw
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.full.hasAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterDecorator {

    @Scan
    companion object {

        @ObjectScanner(KItemDecorator::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: KItemDecorator) {
            if (!data::class.hasAnnotation<RegisterDecorator>()) return

            container.eventBus?.addListener { event: RegisterItemDecorationsEvent ->
                data.targetItems.forEach { event.register(it, data) }
            }


        }
    }
}
