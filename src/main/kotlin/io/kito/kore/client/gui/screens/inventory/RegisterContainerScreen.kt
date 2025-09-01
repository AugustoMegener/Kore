package io.kito.kore.client.gui.screens.inventory

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType.MenuSupplier
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforgespi.language.IModInfo
import java.awt.Menu
import kotlin.reflect.full.hasAnnotation

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterContainerScreen {

    @Scan
    companion object {

        @ObjectScanner(ContainerScreenRegistry::class)
        fun registerScreens(info: IModInfo, container: ModContainer, data: ContainerScreenRegistry<*, *>) {
            if (!data::class.hasAnnotation<RegisterContainerScreen>()) return

            @Suppress(UNCHECKED_CAST)
            data as ContainerScreenRegistry<AbstractContainerMenu, *>

            container.eventBus?.addListener { event: RegisterMenuScreensEvent ->
                event.register(data.menuType(), data.supplier as (T, Inventory, Component) -> Screen & MenuAc<*>)
            }
        }
    }
}
