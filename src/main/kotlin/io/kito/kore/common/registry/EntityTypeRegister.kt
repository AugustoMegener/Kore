package io.kito.kore.common.registry

import io.kito.kore.client.renderer.RendererRegistry
import io.kito.kore.common.capabilities.EntityCapRegister
import io.kito.kore.common.capabilities.EntityCapRegister.EntityCapRegistry
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.early.EarlyRegistry
import io.kito.kore.common.registry.early.EarlyRegistryGoup
import io.kito.kore.common.template.Template
import io.kito.kore.util.Indexable
import io.kito.kore.util.minecraft.ItemProp
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.Builder.of as etBulderOf
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * A utility class for registering custom [EntityType]s in Kore.
 * This class extends [AutoRegister] to allow for automatic registration of entity types
 * with NeoForge, simplifying the process of adding new entities to the game.
 *
 * @property id The mod ID or namespace for these entity types.
 */
open class EntityTypeRegister(final override val id: String) : AutoRegister {

    /**
     * A [DeferredRegister] specifically for [EntityType]s, tied to the given mod ID.
     */
    val register = DeferredRegister.create(ENTITY_TYPE, id)
    /**
     * An [ItemRegister] instance used for registering [SpawnEggItem]s associated with entities.
     */
    val itemRegister = ItemRegister(id)

    /**
     * Infix function to define a new [EntityType] with an entity factory and a mob category.
     * This is the first step in a chain to addEntry a generic entity type.
     *
     * @param T The type of the [Entity] that this type will create.
     * @param name The name of the entity type (e.g., "my_entity").
     * @param pair A [Pair] where the first element is the [EntityFactory] and the second is the [MobCategory].
     * @return An [EntityTypeBuilder] to continue the registration process.
     */
    infix fun <T : Entity> String.of(pair: Pair<(EntityType<T>, Level) -> T, MobCategory>) =
        EntityTypeBuilder(this, pair.first, pair.second)

    /**
     * Infix function to define a new [EntityType] for a [Mob] with an entity factory and a mob category.
     * This is the first step in a chain to addEntry a mob entity type.
     *
     * @param T The type of the [Mob] that this type will create.
     * @param name The name of the mob entity type (e.g., "my_mob").
     * @param pair A [Pair] where the first element is the [EntityFactory] and the second is the [MobCategory].
     * @return A [MobTypeBuilder] to continue the registration process.
     */
    infix fun <T : Mob> String.ofMob(pair: Pair<(EntityType<T>, Level) -> T, MobCategory>) =
        MobTypeBuilder(this, pair.first, pair.second)

    /**
     * Extension property to retrieve the [SpawnEggItem] associated with an [EntityType] property.
     * This is useful for accessing the spawn egg item of a registered entity.
     *
     * @receiver The [KProperty0] representing the [EntityType] property.
     * @return The [DeferredItem] for the [SpawnEggItem] associated with the entity.
     * @throws IllegalStateException if the property does not have a delegation from [EntityRegistry].
     */
    val KProperty0<EntityType<*>>.spawnEgg get() =
        (also { isAccessible = true }.getDelegate() as? EntityRegistry<*>)?.spawnEggRegistry
            ?: throw IllegalStateException("Property does not have a delegation from type ${EntityRegistry::class}")

    /**
     * Extension function to retrieve the [SpawnEggItem] associated with an [EntityType] property, cast to a specific type.
     *
     * @param I The expected type of the [SpawnEggItem].
     * @receiver The [KProperty0] representing the [EntityType] property.
     * @return The [SpawnEggItem] cast to type [I].
     * @throws IllegalStateException if the property does not provide an item of the specified type.
     */
    @Suppress(UNCHECKED_CAST)
    fun <I : SpawnEggItem> KProperty0<EntityType<*>>.spawnEgg() = spawnEgg as? I
        ?: throw IllegalStateException("Property does not provide an item from the specified type")

    /**
     * Open inner class to facilitate the building and registration of [EntityType]s.
     *
     * @param T The type of the [Entity] that this builder handles.
     * @property name The name of the entity type.
     * @property supplier The [EntityFactory] for creating instances of the entity.
     * @property category The [MobCategory] of the entity.
     */
    open inner class EntityTypeBuilder<T : Entity>(val name: String,
                                                   val supplier: EntityFactory<T>,
                                                   val category: MobCategory)
    {
        val entityTypeId = loc(id, name)

        var etBuilder: EntityType.Builder<T>.() -> Unit = {}

        val entityCaps = EntityCaps()

        var renderer: ((EntityRendererProvider.Context) -> EntityRenderer<T, *>)? = null

        var attributes: AttributeSupplier? = null

        /**
         * Configures the properties of the entity type.
         * @param builder A lambda that takes an [EntityType.Builder] and applies properties to it.
         */
        fun props(builder: EntityType.Builder<T>.() -> Unit) { etBuilder = builder }

        /**
         * Sets the [EntityRenderer] for this entity type.
         * @param builder A lambda that takes an [EntityRendererProvider.Context] and returns an [EntityRenderer].
         */
        fun renderer(builder: (EntityRendererProvider.Context) -> EntityRenderer<T, *>) {
            renderer = builder
        }

        /**
         * Configures capabilities for this entity type.
         * @param adder A lambda that takes an [EntityCaps] instance and adds capabilities to it.
         * @return This [EntityTypeBuilder] for fluent chaining.
         */
        fun caps(adder: EntityCaps.() -> Unit) = also { entityCaps.apply(adder) }

        /**
         * Inner class for defining and collecting entity capabilities.
         */
        inner class EntityCaps {
            val registries = arrayListOf<EntityCapRegistry<*, *, *>>()

            /**
             * Operator function to add a capability with a context object.
             * @param O The type of the capability object.
             * @param C The type of the context object.
             * @param getter A lambda that takes an [Entity] and an optional context object, and returns the capability object.
             */
            operator fun <O, C> EntityCapability<O, C>.invoke(getter: (Entity, C?) -> O?) {
                registries += EntityCapRegistry(this, getter)
            }

            /**
             * Operator function to add a capability without a context object (Void?).
             * @param O The type of the capability object.
             * @param getter A lambda that takes an [Entity] and returns the capability object.
             */
            operator fun <O> EntityCapability<O, Void?>.invoke(getter: (Entity) -> O?) {
                registries += EntityCapRegistry(this) { it, _ -> getter(it) }
            }
        }

        /**
         * Finalizes the entity type registration process.
         * This method registers the entity type with NeoForge.
         *
         * @param builder A lambda that takes this [EntityTypeBuilder] and applies additional configurations.
         * @return A [DeferredHolder] for the registered [EntityType].
         */
        open infix fun where(builder: EntityTypeBuilder<T>.() -> Unit): DeferredHolder<EntityType<*>, EntityType<T>> {
            apply(builder)

            val reg = register.register(name) { ->
                etBulderOf(supplier, category).apply(etBuilder).build(
                    ResourceKey.create(Registries.ENTITY_TYPE, loc(id, name))
                )
            }

            attributes?.let { livingAttributes += (reg::value as () -> EntityType<out LivingEntity>) to it }

            EntityCapRegister.entityCaps += reg::value to entityCaps.registries

            renderer?.let { RendererRegistry.entityRenderers += reg::value to it }

            return reg
        }
    }

    /**
     * Inner class for building and registering [Mob] entity types.
     * Extends [EntityTypeBuilder] to provide specific functionalities for mobs, such as spawn eggs.
     */
    inner class MobTypeBuilder<T : Mob>(name: String, supplier: EntityFactory<T>, category: MobCategory) :
        EntityTypeBuilder<T>(name, supplier, category)
    {

        private var spawnEggName = "${name}_spawn_egg"

        private var spawnEggSupplier : (() -> EntityType<T>, ItemProp) -> SpawnEggItem =
            { t, p -> SpawnEggItem(p.spawnEgg(t())) }
        private var spawnEggBuilder  : ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit = {}

        /**
         * Lazily initialized supplier for the [SpawnEggItem] associated with this mob entity.
         */
        val spawnerEgg by lazy { { b: () -> EntityType<T> ->
            itemRegister.ItemBuilder(spawnEggName) { spawnEggSupplier(b, it) }
        } }


        fun spawnEgg(builder: ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit = {}) {
            spawnEggBuilder = builder
        }

        fun spawnEgg(name: String, builder: ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit = {}) {
            spawnEggName = name
            spawnEggBuilder = builder
        }

        fun <I : SpawnEggItem> spawnEgg(supplier: (() -> EntityType<T>, ItemProp) -> I,
                                        builder: ItemRegister.ItemBuilder<I>.() -> Unit)
        {
            spawnEggSupplier = supplier
            spawnEggBuilder = builder as ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit
        }

        /**
         * Finalizes the mob entity type registration process.
         * This method registers the mob entity type and its spawn egg with NeoForge.
         *
         * @param builder A lambda that takes this [MobTypeBuilder] and applies additional configurations.
         * @return An [EntityRegistry] instance containing the registered components.
         * @throws IllegalStateException if `attributes {}` block is not provided for mob types.
         */
        infix fun that(builder: MobTypeBuilder<T>.() -> Unit): EntityRegistry<T> {
            apply(builder)

            val reg = register.register(name) { ->
                etBulderOf(supplier, category).apply(etBuilder).build(
                    ResourceKey.create(Registries.ENTITY_TYPE, loc(id, name))
                )
            }

            EntityCapRegister.entityCaps += reg::value to entityCaps.registries

            attributes?.let { livingAttributes += (reg::value as () -> EntityType<out LivingEntity>) to it } ?:
                throw IllegalStateException("Mob types registries allways need an `attributes {}` block")

            renderer?.let { RendererRegistry.entityRenderers += reg::value to it }

            return EntityRegistry(reg, spawnerEgg(reg::get) where spawnEggBuilder)
        }

        /**
         * Overrides the `where` infix function to prevent its use with [MobTypeBuilder].
         * Mob entity types should use `that` instead of `where` to ensure attributes are defined.
         * @throws IllegalStateException always, instructing to use `that` instead.
         */
        override fun where(builder: EntityTypeBuilder<T>.() -> Unit): DeferredHolder<EntityType<*>, EntityType<T>> {
            throw IllegalStateException("Please use `that` instead from `where` on MobTypeBuilder")
        }
    }


    /**
     * A data class representing the registered components of an entity.
     * This class holds references to the [DeferredHolder] for the entity type and the [DeferredItem] for its spawn egg.
     *
     * @param T The type of the [Entity] that is registered.
     * @property entityRegistry The [DeferredHolder] for the registered entity type.
     * @property spawnEggRegistry The [DeferredItem] for the [SpawnEggItem] associated with the entity.
     */
    inner class EntityRegistry<T : Entity>(val entityRegistry : DeferredHolder<EntityType<*>, EntityType<T>>,
                                           val spawnEggRegistry : DeferredItem<out SpawnEggItem>)
    {
        /**
         * Delegate function for property access, returning the registered [EntityType] instance.
         */
        operator fun getValue(obj: Any,     property: KProperty<*>) : EntityType<T> = entityRegistry.value()
        /**
         * Delegate function for property access, returning the registered [EntityType] instance.
         */
        operator fun getValue(obj: Nothing, property: KProperty<*>) : EntityType<T> = entityRegistry.value()

        /**
         * The [ResourceLocation] key of the registered entity type.
         */
        val key get() = entityRegistry.key
    }

    /**
     * Creates an [EntityTypeTemplate] for entities without a specific spawn egg item type.
     * @param T The type of the index for the template.
     * @param E The type of the [Entity].
     * @param builder A lambda that takes an index of type [T] and returns an [EntityRegistry].
     * @return A new [EntityTypeTemplate] instance.
     */
    fun <T, E : Entity> entityTypeTemplate(registry: EarlyRegistry<T>, builder: (T) -> EntityRegistry<E>) =
        EntityTypeTemplate<T, E, Nothing>(registry, builder)

    /**
     * Creates an [EntityTypeTemplate] for entities with a specific spawn egg item type.
     * @param T The type of the index for the template.
     * @param E The type of the [LivingEntity].
     * @param I The type of the [SpawnEggItem].
     * @param builder A lambda that takes an index of type [T] and returns an [EntityRegistry].
     * @return A new [EntityTypeTemplate] instance.
     */
    fun <T, E : LivingEntity, I : SpawnEggItem> entityTypeTemplateWithEgg(registry: EarlyRegistry<T>,
                                                                          builder: (T) -> EntityRegistry<E>) =
        EntityTypeTemplate<T, E, I>(registry, builder)

    /**
     * A template class for registering multiple entity types based on an index.
     * This allows for defining a common structure for a set of related entities.
     *
     * @param T The type of the index used to identify individual entities within the template.
     * @param E The base type of the [Entity]s in this template.
     * @param I The type of the [SpawnEggItem]s associated with the entities in this template.
     */
    class EntityTypeTemplate<T, E : Entity, I : SpawnEggItem>(override val registry: EarlyRegistry<T>,
                                                              val builder: (T) -> EntityRegistry<E>

    ) :
        Template<T, EntityType<E>>
    {
        // This map will store the *actually* registered entity types, after the call to register()
        private val registeredEntries = hashMapOf<T, EntityRegistry<E>>()

        /**
         * An [Indexable] property to access the [SpawnEggItem]s by their index.
         */
        val egg = object : Indexable<T, I?> { override fun get(idx: T) = registeredEntries[idx]?.spawnEggRegistry?.get() as I? }

        /**
         * Retrieves an [EntityType] by its index.
         * @param idx The index of the entity type.
         * @return The [EntityType] instance, or `null` if not found.
         */
        override fun get(idx: T): EntityType<E>? = registeredEntries[idx]?.entityRegistry?.get()

        override val indexesIds = arrayListOf<ResourceLocation>()
        val groups = arrayListOf<EarlyRegistryGoup>()

        override fun putAllIndexes() { indexesIds += registry.idxs }

        override fun putIndex(id: ResourceLocation) { indexesIds += id }

        override fun putIndex(group: EarlyRegistryGoup) { groups += group }

        override fun register() {
            indexesIds += groups.flatMap { registry.groups[it]!!.map { i -> registry.locationOf(i)!! } }
            indexesIds.distinct().let { indexesIds.clear(); indexesIds += it }

            indexes.forEach { registeredEntries[it] = builder(it) }
        }
    }


    /**
     * Registers the [DeferredRegister]s with the provided [IEventBus].
     * This method is called by Kore during mod initialization to addEntry all defined entity types and their associated items.
     *
     * @param bus The [IEventBus] to addEntry with (typically the Mod Event Bus).
     */
    override fun register(bus: IEventBus) {
        register.register(bus)
        itemRegister.register(bus)
    }

    /**
     * Companion object providing utility functions and event handling for entity types.
     * Annotated with `@Scan` to be automatically discovered by Kore.
     */
    @Scan
    companion object {
        /**
         * A list of pairs, where each pair consists of a supplier for an [EntityType] of a [LivingEntity]
         * and its corresponding [AttributeSupplier].
         */
        private val livingAttributes = arrayListOf<Pair<() -> EntityType<out LivingEntity>, AttributeSupplier>>()

        /**
         * Event subscriber for [EntityAttributeCreationEvent].
         * This function is annotated with [KSubscribe] to be automatically registered by Kore.
         * It registers all collected living entity attributes with the event.
         *
         * @receiver The [EntityAttributeCreationEvent] instance.
         */
        @KSubscribe
        fun EntityAttributeCreationEvent.registerAttributes() {
            livingAttributes.forEach { (et, ats) -> put(et(), ats) }
        }

        /**
         * Extension function for [EntityTypeBuilder] of [LivingEntity] to define attributes for the entity.
         * @param builder A lambda that takes an [AttributeSupplier.Builder] and applies attributes to it.
         */
        fun EntityTypeBuilder<out LivingEntity>.attributes(builder: AttributeSupplier.Builder.() -> Unit) {
            attributes = AttributeSupplier.builder().apply(builder).build()
        }
    }
}

