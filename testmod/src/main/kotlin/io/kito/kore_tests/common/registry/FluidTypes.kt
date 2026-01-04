package io.kito.kore_tests.common.registry

import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.client
import io.kito.kore.common.event.RegisterTemplate
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.FluidTypeRegister
import io.kito.kore.common.template.Template.Companion.include
import io.kito.kore.util.minecraft.EN_US
import io.kito.kore.util.minecraft.PT_BR
import io.kito.kore.util.minecraft.ResourceLocationExt.block
import io.kito.kore.util.minecraft.ResourceLocationExt.item
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import io.kito.kore.util.toTitle
import io.kito.kore_tests.DataGenerator.cubeAllModel
import io.kito.kore_tests.DataGenerator.defaultModel
import io.kito.kore_tests.DataGenerator.flatModel
import io.kito.kore_tests.DataGenerator.model
import io.kito.kore_tests.DataGenerator.named
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import io.kito.kore_tests.common.registry.early.Strings.myGroup
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.world.item.Items.BUCKET
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder
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
                named(EN_US to "My Fluid Bucket",
                      PT_BR to "Meu Balde de Fluido")

                props {
                    stacksTo(1)
                    craftRemainder(BUCKET)
                }

                defaultModel()
            }

            liquidBlock {
                cubeAllModel()
            }
        }

        client {
            color = 0x7F745094
            stillTexturePath = loc("water_still").block
            flowingTexturePath = loc("water_flow").block
        }
    }

    @RegisterTemplate
    val fluidTemplate = fluidTypeTemplate(stringRegistry) { i: String ->
        "${i}_fluid" of ::FluidType where {
            flowingFluid {
                props {
                    slopeFindDistance(2)
                    levelDecreasePerBlock(1)
                }

                bucketItem {
                    named(EN_US to "${i.toTitle()} Fluid Bucket",
                          PT_BR to "Balde de Fluido ${i.toTitle()}")

                    props {
                        stacksTo(1)
                        craftRemainder(BUCKET)
                    }

                    flatModel(local("my_fluid_bucket"))
                }

                liquidBlock {
                    cubeAllModel()
                }
            }

            client {
                color = 0x7F745094
                stillTexturePath = loc("water_still").block
                flowingTexturePath = loc("water_flow").block
            }
        }
    }.include(myGroup)}

