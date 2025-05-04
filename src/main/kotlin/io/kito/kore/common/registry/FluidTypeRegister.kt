package io.kito.kore.common.registry

import io.kito.kore.common.registry.BlockRegister.BlockRegistry
import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidBuilder
import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidRegistry
import io.kito.kore.common.template.Template
import io.kito.kore.util.Indexable
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.FluidTypeProp
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible


open class FluidTypeRegister(final override val id: String) : AutoRegister {

    private val register = DeferredRegister.create(FLUID_TYPES, id)
    private val flowingRegister = FlowingFluidRegister(id)

    infix fun String.of(supplier: (FluidTypeProp) -> FluidType) = FluidTypeBuilder(this, supplier)

    inner class FluidTypeBuilder(val name: String, val supplier: (FluidTypeProp) -> FluidType) {

        val id = this@FluidTypeRegister.id

        val fluidTypeRegistry by lazy { register.register(name) { -> supplier(prop) } }

        private val prop = FluidTypeProp.create()

        private var flowingFluidBuinder: FlowingFluidBuilder.() -> Unit = {}

        var makeFlowingFluid = true

        fun props(block: FluidTypeProp.() -> Unit) { prop.apply(block) }

        fun flowingFluid(block: FlowingFluidBuilder.() -> Unit) { flowingFluidBuinder = block }

        infix fun where(action: FluidTypeBuilder.() -> Unit) : FluidTypeRegistry {
            apply(action)
            val registry = fluidTypeRegistry

            return FluidTypeRegistry(
                registry,
                if (makeFlowingFluid) with(flowingRegister) { name from registry::get where flowingFluidBuinder }
                else null
            )
        }
    }

    val KProperty0<FluidType>.flowingFluid get() =
        (also { isAccessible = true }.getDelegate() as? FluidTypeRegistry)?.flowingRegistry
            ?: throw IllegalStateException("Property does not have a delegation from type ${BlockRegistry::class}")

    class FluidTypeRegistry(val registry: DeferredHolder<FluidType, FluidType>,
                            val flowingRegistry: FlowingFluidRegistry?)
    {
        operator fun getValue(cls: Any, prop: KProperty<*>): FluidType = registry.get()
    }

    class FluidTypeTemplate<T,  B: LiquidBlock, I: BucketItem>(val builder: (T) -> FluidTypeRegistry) :
        Template<T, FluidType>
    {
        private val entries = hashMapOf<T, FluidTypeRegistry>()

        override val allIdxs by lazy { entries.keys }

        val flowingFluid = FlowingFluidRegister.FlowingFluidTemplate<T, B, I> { entries[it]!!.flowingRegistry!! }

        override fun get(idx: T): FluidType? = entries[idx]?.registry?.get()

        override fun register(vararg idxs: T) {
            idxs.forEach {
                entries[it] = builder(it)
                if (entries[it]?.flowingRegistry != null) flowingFluid.register(it)
            }
        }
    }

    fun <T> fluidTypeTemplate(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, BucketItem>(builder)

    fun <T, B: LiquidBlock> fluidTypeTemplateWithLiquidBlock(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, BucketItem>(builder)

    fun <T, I: BucketItem> fluidTypeTemplateWithBucketItem(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, I>(builder)

    fun <T, B: LiquidBlock, I: BucketItem> fluidTypeTemplateFull(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, I>(builder)

    override fun register(bus: IEventBus) {
        register.register(bus)
        flowingRegister.register(bus)
    }
}