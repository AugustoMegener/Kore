package io.kito.kore_tests.common.registry

import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.client
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.FluidTypeRegister
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.bucketModel
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
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

    val fluidTemplate = fluidTypeTemplate { i: String ->
        "${i}_fluid" of ::FluidType where {
            flowingFluid {
                props {
                    slopeFindDistance(2)
                    levelDecreasePerBlock(1)
                }

                bucketItem {
                    props {
                        named(EN_US to "${i.toTitle()} Fluid Bucket",
                              PT_BR to "Balde de Fluido ${i.toTitle()}")

                        stacksTo(1)
                        craftRemainder(BUCKET)

                        model { loc, _ ->
                            withExistingParent("$loc", mcLoc("item/generated"))
                                .texture("layer0", mcLoc("bucket").item)
                                .texture("layer1", local("my_fluid_bucket").item)
                        }
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
}