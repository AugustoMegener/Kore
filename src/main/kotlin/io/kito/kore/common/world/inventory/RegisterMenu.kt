package io.kito.kore.common.world.inventory

import io.kito.kore.common.reflect.ClassScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.core.registries.BuiltInRegistries.MENU
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterMenu(val name: String) {

    @Scan
    companion object {

        private val mTypes = hashMapOf<KClass<out AbstractContainerMenu>, () -> MenuType<*>>()

        private val menuSupplierArgumentTypes = listOf(Int::class, Inventory::class, RegistryFriendlyByteBuf::class)

        @Suppress(UNCHECKED_CAST)
        fun <T : AbstractContainerMenu> menuType(clazz: KClass<T>) = mTypes[clazz]!!() as MenuType<T>

        @ClassScanner(AbstractContainerMenu::class)
        fun registerMenus(info: IModInfo, container: ModContainer, data: KClass<out AbstractContainerMenu>) {
            if (!data.hasAnnotation<RegisterMenu>()) return

            val clientConstructor =
                data.constructors.find { c -> c.parameters.map { it.type.jvmErasure } == menuSupplierArgumentTypes }
                    ?: throw IllegalStateException(
                        "No constructor with Int, Inventory and RegistryFriendlyByteBuf found on $data"
                    )

            with(DeferredRegister.create(MENU, container.modId)) {
                val reg = register(data.findAnnotation<RegisterMenu>()!!.name) { ->
                    IMenuTypeExtension.create { id, inv, buff -> clientConstructor.call(id, inv, buff) }
                }
                register(container.eventBus!!)

                mTypes[data] = { reg.get() }
            }
        }
    }
}