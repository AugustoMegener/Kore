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


/**
 * A utility class for registering custom [FluidType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of fluid types
 * with NeoForge, simplifying the process of adding new fluid types to the game.
 *
 * @property id The mod ID or namespace for these registrations.
 */
open class FluidTypeRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [FluidType]s, tied to the given mod ID.
     */
    private val register = DeferredRegister.create(FLUID_TYPES, id)
    /**
     * A [FlowingFluidRegister] instance used for registering [FlowingFluid]s associated with fluid types.
     */
    private val flowingRegister = FlowingFluidRegister(id)

    /**
     * Infix function to define a new [FluidType] with a supplier for creating [FluidType] instances.
     * This is the first step in a chain to addEntry a fluid type.
     *
     * @param name The name of the fluid type (e.g., "my_fluid_type").
     * @param supplier A lambda that supplies a new instance of the [FluidType] given [FluidTypeProp].
     * @return A [FluidTypeBuilder] to continue the registration process.
     */
    infix fun String.of(supplier: (FluidTypeProp) -> FluidType) = FluidTypeBuilder(this, supplier)

    /**
     * Inner class to facilitate the building and registration of [FluidType]s.
     *
     * @property name The name of the fluid type.
     * @property supplier A lambda that supplies a new instance of the [FluidType].
     */
    inner class FluidTypeBuilder(val name: String, val supplier: (FluidTypeProp) -> FluidType) {

        val id = this@FluidTypeRegister.id

        /**
         * Lazily initialized [DeferredHolder] for the registered [FluidType].
         */
        val fluidTypeRegistry by lazy { register.register(name) { -> supplier(prop) } }

        private val prop = FluidTypeProp.create()

        private var flowingFluidBuinder: FlowingFluidBuilder.() -> Unit = {}

        /**
         * Flag to control whether a [FlowingFluid] should be created for this fluid type.
         */
        var makeFlowingFluid = true

        /**
         * Configures the properties of the fluid type.
         * @param block A lambda that takes a [FluidTypeProp] instance and applies properties to it.
         */
        fun props(block: FluidTypeProp.() -> Unit) { prop.apply(block) }

        /**
         * Configures the [FlowingFluidBuilder] for this fluid type.
         * @param block A lambda that takes a [FlowingFluidBuilder] and applies configurations.
         */
        fun flowingFluid(block: FlowingFluidBuilder.() -> Unit) { flowingFluidBuinder = block }

        /**
         * Finalizes the fluid type registration process.
         * This method registers the fluid type and optionally its associated flowing fluid.
         *
         * @param action A lambda that takes this [FluidTypeBuilder] and applies additional configurations.
         * @return A [FluidTypeRegistry] instance containing the registered components.
         */
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

    /**
     * Extension property to retrieve the [FlowingFluidRegistry] associated with a [FluidType] property.
     * This is useful for accessing the flowing fluid components of a registered fluid type.
     *
     * @receiver The [KProperty0] representing the [FluidType] property.
     * @return The [FlowingFluidRegistry] associated with the fluid type.
     * @throws IllegalStateException if the property does not have a delegation from [FluidTypeRegistry].
     */
    val KProperty0<FluidType>.flowingFluid get() =
        (also { isAccessible = true }.getDelegate() as? FluidTypeRegistry)?.flowingRegistry
            ?: throw IllegalStateException("Property does not have a delegation from type ${BlockRegistry::class}")

    /**
     * A data class representing the registered components of a fluid type.
     * This class holds references to the [DeferredHolder] for the fluid type and its optional [FlowingFluidRegistry].
     *
     * @property registry The [DeferredHolder] for the registered fluid type.
     * @property flowingRegistry The optional [FlowingFluidRegistry] associated with the fluid type.
     */
    class FluidTypeRegistry(val registry: DeferredHolder<FluidType, FluidType>,
                            val flowingRegistry: FlowingFluidRegistry?)
    {
        /**
         * Delegate function for property access, returning the registered [FluidType] instance.
         */
        operator fun getValue(cls: Any, prop: KProperty<*>): FluidType = registry.get()
    }

    /**
     * A template class for registering multiple fluid types based on an index.
     * This allows for defining a common structure for a set of related fluid types.
     *
     * @param T The type of the index used to identify individual fluid types within the template.
     * @param B The base type of the [LiquidBlock]s associated with the fluids in this template.
     * @param I The type of the [BucketItem]s associated with the fluids in this template.
     */
    class FluidTypeTemplate<T, B: LiquidBlock, I: BucketItem>(val builder: (T) -> FluidTypeRegistry) :
        Template<T, FluidType>
    {
        // This map will store the *actually* registered fluid type registries, after the call to register()
        private val registeredEntries = hashMapOf<T, FluidTypeRegistry>()

        // This list will store the index suppliers passed to addEntry for this template
        private val pendingEntrySuppliers = mutableListOf<() -> T>()

        // allIdxs should reflect the indices of fluid types that have been effectively registered
        override val allIdxs by lazy { registeredEntries.keys }

        /**
         * A [FlowingFluidRegister.FlowingFluidTemplate] instance to manage the flowing fluids associated with this template.
         * Its builder relies on the FluidTypeRegistry being present in 'registeredEntries' when its 'register()' is called.
         */
        val flowingFluid = FlowingFluidRegister.FlowingFluidTemplate<T, B, I> { registeredEntries[it]!!.flowingRegistry!! }

        /**
         * Retrieves a [FluidType] by its index.
         * @param idx The index of the fluid type.
         * @return The [FluidType] instance, or `null` if not found.
         */
        override fun get(idx: T): FluidType? = registeredEntries[idx]?.registry?.get()

        /**
         * Adds the index suppliers to the pending list for this template and also for the associated flowing fluid template.
         * The invocation of suppliers and the actual registration will occur in register().
         *
         * @param idxs A vararg of suppliers for the indices.
         */
        override fun addEntry(vararg idxs: () -> T) {
            pendingEntrySuppliers.addAll(idxs)
            // Also pass the suppliers to the nested flowingFluid template
            flowingFluid.addEntry(*idxs)
        }

        /**
         * Performs the registration of fluid types.
         * Invokes all index suppliers added via addEntry for this template,
         * registers them, and then triggers the registration for the associated flowing fluids.
         */
        override fun register() {
            // First, process and register the entries for this FluidTypeTemplate
            pendingEntrySuppliers.forEach { supplier ->
                val idx = supplier() // HERE is where the supplier is invoked for FluidTypeTemplate
                registeredEntries[idx] = builder(idx)
            }
            pendingEntrySuppliers.clear() // Clear pending suppliers after processing

            // After this template's entries are registered, trigger the registration for flowingFluid.
            // At this point, 'registeredEntries[idx]' will contain the necessary FluidTypeRegistry
            // for the flowingFluid's builder to work.
            flowingFluid.register()
        }
    }

    /**
     * Creates a [FluidTypeTemplate] for fluid types without a specific liquid block or bucket item type.
     * @param T The type of the index for the template.
     * @param builder A lambda that takes an index of type [T] and returns a [FluidTypeRegistry].
     * @return A new [FluidTypeTemplate] instance.
     */
    fun <T> fluidTypeTemplate(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, BucketItem>(builder)

    /**
     * Creates a [FluidTypeTemplate] for fluid types with a specific liquid block type.
     * @param T The type of the index for the template.
     * @param B The type of the [LiquidBlock].
     * @param builder A lambda that takes an index of type [T] and returns a [FluidTypeRegistry].
     * @return A new [FluidTypeTemplate] instance.
     */
    fun <T, B: LiquidBlock> fluidTypeTemplateWithLiquidBlock(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, BucketItem>(builder)

    /**
     * Creates a [FluidTypeTemplate] for fluid types with a specific bucket item type.
     * @param T The type of the index for the template.
     * @param I The type of the [BucketItem].
     * @param builder A lambda that takes an index of type [T] and returns a [FluidTypeRegistry].
     * @return A new [FluidTypeTemplate] instance.
     */
    fun <T, I: BucketItem> fluidTypeTemplateWithBucketItem(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, LiquidBlock, I>(builder)

    /**
     * Creates a [FluidTypeTemplate] for fluid types with both a specific liquid block type and a specific bucket item type.
     * @param T The type of the index for the template.
     * @param B The type of the [LiquidBlock].
     * @param I The type of the [BucketItem].
     * @param builder A lambda that takes an index of type [T] and returns a [FluidTypeRegistry].
     * @return A new [FluidTypeTemplate] instance.
     */
    fun <T, B: LiquidBlock, I: BucketItem> fluidTypeTemplateFull(builder: (T) -> FluidTypeRegistry) =
        FluidTypeTemplate<T, B, I>(builder)

    /**
     * Registers the [DeferredRegister]s with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined fluid types and their associated flowing fluids.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register.register(bus)
        flowingRegister.register(bus)
    }
}

