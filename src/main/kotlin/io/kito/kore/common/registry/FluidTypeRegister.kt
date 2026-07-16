package io.kito.kore.common.registry

import io.kito.kore.common.registry.BlockRegister.BlockRegistry
import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidBuilder
import io.kito.kore.common.registry.FlowingFluidRegister.FlowingFluidRegistry
import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGroup
import io.kito.kore.common.template.Template
import io.kito.kore.util.minecraft.FluidTypeProp
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.block.LiquidBlock
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

        val fluidTypeId = loc(id, name)

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

    class FluidTypeTemplate<T, B: LiquidBlock, I: BucketItem>(override val registry: EarlyRegistry<T>,
                                                              val builder: (T) -> FluidTypeRegistry)
        : Template<T, FluidType>
    {
        private val registeredEntries = hashMapOf<T, FluidTypeRegistry>()

        val flowingFluid =
            FlowingFluidRegister.FlowingFluidTemplate<T, B, I>(registry) { registeredEntries[it]!!.flowingRegistry!! }

        override fun get(idx: T): FluidType? = registeredEntries[idx]?.registry?.get()

        override val indexesIds = arrayListOf<ResourceLocation>()
        val groups = arrayListOf<EarlyRegistryGroup>()

        override fun putAllIndexes() { indexesIds += registry.idxs }

        override fun putIndex(id: ResourceLocation) { indexesIds += id }

        override fun putIndex(group: EarlyRegistryGroup) { groups += group }

        override fun register() {
            indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
            indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

            indexes.forEach { registeredEntries[it] = builder(it) }

            flowingFluid.indexesIds += indexesIds
            flowingFluid.register()
        }
    }

    fun <T> fluidTypeTemplate(registry: EarlyRegistry<T>,builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, BucketItem>(registry, builder)

    fun <T, B: LiquidBlock> fluidTypeTemplateWithLiquidBlock(registry: EarlyRegistry<T>, builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, BucketItem>(registry, builder)

    fun <T, I: BucketItem> fluidTypeTemplateWithBucketItem(registry: EarlyRegistry<T>, builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, I>(registry, builder)

    fun <T, B: LiquidBlock, I: BucketItem> fluidTypeTemplateFull(registry: EarlyRegistry<T>, builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, I>(registry, builder)

    override fun register(bus: IEventBus) {
        register.register(bus)
        flowingRegister.register(bus)
    }
}

