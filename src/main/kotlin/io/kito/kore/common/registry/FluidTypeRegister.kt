package io.kito.kore.common.registry

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidType.Properties
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

class FluidTypeRegister(override val id: String) : AutoRegister {

    private val registry = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, id)


    class FluidTypeBuilder(val supplier: (Properties) -> FluidType)

    override fun register(bus: IEventBus) {
        registry.register("") { ->
            FluidType(Properties.create())
        }
        TODO("Not yet implemented")
    }
}