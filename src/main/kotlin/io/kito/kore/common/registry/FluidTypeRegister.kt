package io.kito.kore.common.registry

import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidBuilder
import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidRegistry
import io.kito.kore.util.minecraft.FluidTypeProp
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES
import kotlin.reflect.KProperty


open class FluidTypeRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.create(FLUID_TYPES, id)
    private val flowingRegister = FlowingFluidRegister(id)

    infix fun String.of(supplier: (FluidTypeProp) -> FluidType) = FluidTypeBuilder(this, supplier)

    inner class FluidTypeBuilder<T: FluidType>(val name: String, val supplier: (FluidTypeProp) -> T) {

        val id = this@FluidTypeRegister.id

        val fluidTypeRegistry by lazy { register.register(name) { -> supplier(prop) } }

        private val prop = FluidTypeProp.create()

        private var flowingFluidBuinder: FlowingFluidBuilder.() -> Unit = {}

        var makeFlowingFluid = true

        fun props(block: FluidTypeProp.() -> Unit) { prop.apply(block) }

        fun flowingFluid(block: FlowingFluidBuilder.() -> Unit) { flowingFluidBuinder = block }

        infix fun where(action: FluidTypeBuilder<T>.() -> Unit) : FluidTypeRegistry<T> {
            apply(action)
            val registry = fluidTypeRegistry

            return FluidTypeRegistry(
                registry,
                if (makeFlowingFluid) with(flowingRegister) { name from registry::get where flowingFluidBuinder }
                else null
            )
        }
    }

    class FluidTypeRegistry<T: FluidType>(val registry: DeferredHolder<FluidType, T>,
                                          val flowingRegistry: FlowingFluidRegistry?) {
        operator fun getValue(cls: Any, prop: KProperty<*>) = registry.get()
    }

    override fun register(bus: IEventBus) {
        register.register(bus)
        flowingRegister.register(bus)
    }
}