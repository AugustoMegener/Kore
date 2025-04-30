package io.kito.kore_tests.common.registry

import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.client
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.FluidTypeRegister
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.loc
import io.kito.kore.util.neoforge.ResourceLocationExt.block
import io.kito.kore_tests.DataGenerator.bucketModel
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import net.minecraft.world.item.Items.BUCKET
import net.neoforged.neoforge.fluids.FluidType

@Scan
object FluidTypes : FluidTypeRegister(ID) {

    val myFluid by "my_fluid" of ::FluidType where {

        flowingFluid {
            props {
                slopeFindDistance(2)
                levelDecreasePerBlock(1)
            }

            bucketItem {
                props {
                    named(EN_US to "My Fluid Bucket",
                          PT_BR to "Meu Balde de Fluido")

                    stacksTo(1)
                    craftRemainder(BUCKET)

                    bucketModel()
                }
            }
        }

        client {
            color = 0x7F745094
            stillTexturePath = loc("water_still").block
            flowingTexturePath = loc("water_flow").block
        }
    }
}