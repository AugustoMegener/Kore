package io.kito.kore.common.registry

import io.kito.kore.client.renderer.RendererRegistry
import io.kito.kore.common.capabilities.EntityCapRegister
import io.kito.kore.common.capabilities.EntityCapRegister.EntityCapRegistry
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.ItemProp
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.loc
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
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
import net.neoforged.neoforge.common.DeferredSpawnEggItem
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class EntityTypeRegister(final override val id: String) : AutoRegister {

    val register = DeferredRegister.create(ENTITY_TYPE, id)
    val itemRegister = ItemRegister(id)

    infix fun <T : Entity> String.of(pair: Pair<(EntityType<T>, Level) -> T, MobCategory>) =
        EntityTypeBuilder(this, pair.first, pair.second)

    infix fun <T : Mob> String.ofMob(pair: Pair<(EntityType<T>, Level) -> T, MobCategory>) =
        MobTypeBuilder(this, pair.first, pair.second)

    val KProperty0<EntityType<*>>.spawnEgg get() =
        (also { isAccessible = true }.getDelegate() as? EntityRegistry<*>)?.spawnEggRegistry
            ?: throw IllegalStateException("Property does not have a delegation of type ${EntityRegistry::class}")

    @Suppress(UNCHECKED_CAST)
    fun <I : SpawnEggItem> KProperty0<EntityType<*>>.spawnEgg() = spawnEgg as? I
        ?: throw IllegalStateException("Property does not provide an item of the specified type")

    open inner class EntityTypeBuilder<T : Entity>(val name: String,
                                                   val supplier: EntityFactory<T>,
                                                   val category: MobCategory)
    {
        var etBuilder: EntityType.Builder<T>.() -> Unit = {}

        val entityCaps = EntityCaps()

        var renderer: ((EntityRendererProvider.Context) -> EntityRenderer<T>)? = null

        var attributes: AttributeSupplier? = null

        fun props(builder: EntityType.Builder<T>.() -> Unit) { etBuilder = builder }

        fun renderer(builder: (EntityRendererProvider.Context) -> EntityRenderer<T>) {
            renderer = builder
        }

        fun caps(adder: EntityCaps.() -> Unit) = also { entityCaps.apply(adder) }

        inner class EntityCaps {
            val registries = arrayListOf<EntityCapRegistry<*, *, *>>()

            operator fun <O, C> EntityCapability<O, C>.invoke(getter: (Entity, C?) -> O) {
                registries += EntityCapRegistry(this, getter)
            }

            operator fun <O> EntityCapability<O, Void?>.invoke(getter: (Entity) -> O) {
                registries += EntityCapRegistry(this) { it, _ -> getter(it) }
            }
        }

        open infix fun where(builder: EntityTypeBuilder<T>.() -> Unit): DeferredHolder<EntityType<*>, EntityType<T>> {
            apply(builder)

            val reg = register.register(name)
                { -> etBulderOf(supplier, category).apply(etBuilder).build(loc(id, name).toString()) }

            attributes?.let { livingAttributes += (reg::value as () -> EntityType<out LivingEntity>) to it }

            EntityCapRegister.entityCaps += reg::value to entityCaps.registries

            renderer?.let { RendererRegistry.entityRenderers += reg::value to it }

            return reg
        }
    }

    inner class MobTypeBuilder<T : Mob>(name: String, supplier: EntityFactory<T>, category: MobCategory) :
        EntityTypeBuilder<T>(name, supplier, category)
    {
        private var spawnEggColor1 : Int = 0
        private var spawnEggColor2 : Int = 0

        private var spawnEggSupplier : (() -> EntityType<T>, Int, Int, ItemProp) -> SpawnEggItem =
            { t, c1, c2, p -> DeferredSpawnEggItem(t, c1, c2, p) }
        private var spawnEggBuilder  : ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit = {}

        val spawnerEgg by lazy { { b: () -> EntityType<T> ->
            itemRegister.ItemBuilder(name) { spawnEggSupplier(b, spawnEggColor1, spawnEggColor2, it) }
        } }

        fun spawnEgg(c1: Int, c2: Int, builder: ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit = {}) {
            spawnEggColor1 = c1
            spawnEggColor2 = c2
            spawnEggBuilder = builder
        }

        fun <I : SpawnEggItem> spawnEgg(c1: Int,
                                        c2: Int,
                                        supplier: (() -> EntityType<T>, Int, Int, ItemProp) -> I,
                                        builder: ItemRegister.ItemBuilder<I>.() -> Unit)
        {
            spawnEggColor1 = c1
            spawnEggColor2 = c2
            spawnEggSupplier = supplier
            spawnEggBuilder = builder as ItemRegister.ItemBuilder<out SpawnEggItem>.() -> Unit
        }

        infix fun that(builder: MobTypeBuilder<T>.() -> Unit): EntityRegistry<T> {
            apply(builder)

            val reg = register.register(name)
            { -> etBulderOf(supplier, category).apply(etBuilder).build(loc(id, name).toString()) }

            EntityCapRegister.entityCaps += reg::value to entityCaps.registries

            attributes?.let { livingAttributes += (reg::value as () -> EntityType<out LivingEntity>) to it } ?:
                throw IllegalStateException("Mob types registries allways need an `attributes {}` block")

            renderer?.let { RendererRegistry.entityRenderers += reg::value to it }

            return EntityRegistry(reg, spawnerEgg(reg::get) where spawnEggBuilder)
        }

        override fun where(builder: EntityTypeBuilder<T>.() -> Unit): DeferredHolder<EntityType<*>, EntityType<T>> {
            throw IllegalStateException("Please use `that` instead of `where` on MobTypeBuilder")
        }
    }


    inner class EntityRegistry<T : Entity>(val entityRegistry : DeferredHolder<EntityType<*>, EntityType<T>>,
                                           val spawnEggRegistry : DeferredItem<out SpawnEggItem>)
    {
        operator fun getValue(obj: Any,     property: KProperty<*>) : EntityType<T> = entityRegistry.value()
        operator fun getValue(obj: Nothing, property: KProperty<*>) : EntityType<T> = entityRegistry.value()

        val key get() = entityRegistry.key
    }

    override fun register(bus: IEventBus) {
        register.register(bus)
        itemRegister.register(bus)
    }

    @Scan
    companion object {
        private val livingAttributes = arrayListOf<Pair<() -> EntityType<out LivingEntity>, AttributeSupplier>>()

        @KSubscribe
        fun EntityAttributeCreationEvent.registerAttributes() {
            livingAttributes.forEach { (et, ats) -> put(et(), ats) }
        }

        fun EntityTypeBuilder<out LivingEntity>.attributes(builder: AttributeSupplier.Builder.() -> Unit) {
            attributes = AttributeSupplier.builder().apply(builder).build()
        }
    }
}