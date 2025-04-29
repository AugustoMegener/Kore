package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.FluidTypeRegister
import io.kito.kore_tests.ID
import net.neoforged.neoforge.fluids.FluidType

@Scan
object FluidTypes : FluidTypeRegister(ID) {

    val myFluid by "my_fluid" of ::FluidType where {
        props {
            viscosity(5)
        }

        /*client {
            color = 0x7F745094
            stillTexturePath = loc("water_still").block
            flowingTexturePath = loc("water_flow").block
        }*/
    }
}