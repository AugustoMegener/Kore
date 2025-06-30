package io.kito.kore_tests.common.registry

import io.kito.kore.client.renderer.ext.KSimpleClientFluidTypeExt.Companion.client
import io.kito.kore.common.event.RegisterTemplate
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
import io.kito.kore_tests.common.registry.early.Registries.stringRegistry
import net.minecraft.world.item.Items.BUCKET
import net.neoforged.neoforge.fluids.FluidType

/**
 * Registers custom fluid types for the Kore Tests mod.
 * Annotated with `@Scan` to be automatically discovered by Kore for fluid type registration.
 * Extends `FluidTypeRegister` with the mod ID, providing a DSL for defining fluid types.
 */
@Scan
object FluidTypes : FluidTypeRegister(ID) {

    /**
     * Defines a custom fluid named "my_fluid".
     * - `of ::FluidType`: Specifies the fluid type class.
     * - `flowingFluid`: Configures properties for the flowing fluid, such as `slopeFindDistance` and `levelDecreasePerBlock`.
     * - `bucketItem`: Defines the associated bucket item, including its name, stack size, craft remainder, and model.
     * - `client`: Sets client-side rendering properties like color and textures for still and flowing states.
     */
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

    /**
     * A template for defining similar fluid types programmatically.
     * It takes a string `i` to generate unique fluid names and localized bucket names.
     * - `flowingFluid`: Configures common flowing fluid properties.
     * - `bucketItem`: Defines the associated bucket item with dynamic naming and a generic bucket model.
     * - `client`: Sets common client-side rendering properties.
     */
    @RegisterTemplate
    val fluidTemplate = fluidTypeTemplate(stringRegistry) { i: String ->
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

