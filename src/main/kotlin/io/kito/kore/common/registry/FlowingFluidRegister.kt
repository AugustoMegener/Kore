package io.kito.kore.common.registry

import io.kito.kore.common.registry.BlockRegister.BlockBuilder
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.BlockProp
import io.kito.kore.util.minecraft.FlowingFluidProp
import net.minecraft.core.registries.BuiltInRegistries.FLUID
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.PushReaction
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

typealias DeferredFluid<T> = DeferredHolder<Fluid, T>

open class FlowingFluidRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.create(FLUID, id)
    private val blockRegister = BlockRegister(id)

    infix fun String.from(type: () -> FluidType) = FlowingFluidBuilder(this, type)

    inner class FlowingFluidBuilder(val name: String, val type: () -> FluidType) {
        private val prop = { s: DeferredFluid<Source>, f: DeferredFluid<Flowing> ->
            BaseFlowingFluid.Properties(type, s, f).apply(propConfig)
        }

        private var propConfig: FlowingFluidProp.() -> Unit = {}

        private var  sourcePropConfig: FlowingFluidProp.() -> Unit = {}
        private var flowingPropConfig: FlowingFluidProp.() -> Unit = {}

        var makeLiquidBlock = true

        private var liquidBlockSupplier: (FlowingFluid, BlockProp) -> LiquidBlock = ::LiquidBlock
        private var liquidBlockBuilder: BlockBuilder<out LiquidBlock>.() -> Unit = {
            props {
                replaceable()
                noCollission()
                strength(100.0F)
                pushReaction(PushReaction.DESTROY)
                noLootTable()
                liquid()
                sound(SoundType.EMPTY)
            }
        }

        private val flowingRegistry: DeferredHolder<Fluid, Flowing> by lazy {
            register.register("flowing_$name") { -> Flowing(prop(sourceRegistry, flowingRegistry).apply(flowingPropConfig)) }
        }

        private val sourceRegistry: DeferredHolder<Fluid, Source> by lazy {
            register.register("source_$name") { -> Source(prop(sourceRegistry, flowingRegistry).apply(sourcePropConfig)) }
        }

        fun liquidBlock(builder: BlockBuilder<out LiquidBlock>.() -> Unit) { liquidBlockBuilder = builder }

        @Suppress(UNCHECKED_CAST)
        fun <T : LiquidBlock> liquidBlock(supplier: (FlowingFluid, BlockProp) -> T,
                                          builder: BlockBuilder<out T>.() -> Unit)
        { liquidBlockSupplier = supplier
          liquidBlockBuilder = builder as BlockBuilder<out LiquidBlock>.() -> Unit }

        fun props(action: FlowingFluidProp.() -> Unit) { propConfig = action }

        fun  sourceProps(action: FlowingFluidProp.() -> Unit) { sourcePropConfig = action }
        fun flowingProps(action: FlowingFluidProp.() -> Unit) { flowingPropConfig = action }

        infix fun where(action: FlowingFluidBuilder.() -> Unit): FlowingFluidRegistry {
            apply(action)

            val source = sourceRegistry

            return FlowingFluidRegistry(
                source, flowingRegistry,
                if (makeLiquidBlock)
                    with(blockRegister) { name of { liquidBlockSupplier(source.get(), it) } where liquidBlockBuilder }
                else null
            )
        }
    }

    class FlowingFluidRegistry(val source: DeferredHolder<Fluid, Source>,
                               val flowing: DeferredHolder<Fluid, Flowing>,
                               val liquidBlock: BlockRegister.BlockRegistry<out LiquidBlock>?)

    override fun register(bus: IEventBus) {
        register.register(bus)
        blockRegister.register(bus)
    }
}