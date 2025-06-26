package io.kito.kore.common

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KProperty

/**
 * Abstract base class for defining mod configurations in Kore.
 * This class simplifies the creation and registration of configuration specifications
 * for NeoForged mods, allowing for type-safe and organized configuration values.
 *
 * Subclasses should define their configuration properties using the [Value] inner class.
 *
 * @property type The [ModConfig.Type] of this configuration (e.g., CLIENT, SERVER, COMMON).
 */
abstract class KConfig(val type: ModConfig.Type) {

    /**
     * A mutable list to store all [Value] instances defined within this configuration.
     * These values will be used to build the [ModConfigSpec].
     */
    private val values = arrayListOf<Value<*>>()

    /**
     * The [ModConfigSpec] for this configuration.
     * It is lazily initialized by configuring a [ModConfigSpec.Builder] with all defined [Value]s.
     */
    val spec: ModConfigSpec by lazy { ModConfigSpec.Builder().configure(::config).value }

    /**
     * Configures the [ModConfigSpec.Builder] by iterating through all defined [Value]s
     * and allowing them to define their respective configuration entries.
     *
     * @param builder The [ModConfigSpec.Builder] to configure.
     * @return This [KConfig] instance for fluent chaining.
     */
    fun config(builder: ModConfigSpec.Builder) : KConfig {
        values.forEach {
            it.configure(builder)
        }
        return this
    }

    /**
     * Inner class representing a single configurable value within a [KConfig].
     * It uses Kotlin's delegated properties to provide a convenient way to define
     * and access configuration values.
     *
     * @param T The type of the configuration value.
     * @param getter A lambda that takes a [ModConfigSpec.Builder] and returns a [ModConfigSpec.ConfigValue] of type [T].
     *               This lambda is responsible for defining the configuration entry (e.g., `builder.define("key", defaultValue)`).
     */
    inner class Value<T>(private val getter: ModConfigSpec.Builder.() -> ModConfigSpec.ConfigValue<T>) {

        private lateinit var configValue: ModConfigSpec.ConfigValue<T>

        init { values += this }

        /**
         * Configures the underlying [ModConfigSpec.ConfigValue] using the provided [ModConfigSpec.Builder].
         * This method is called internally during the [KConfig.config] process.
         * @param builder The [ModConfigSpec.Builder] to use for defining the configuration value.
         */
        fun configure(builder: ModConfigSpec.Builder) { configValue = getter(builder) }

        /**
         * Provides the value of the configuration entry when accessed as a delegated property.
         * @param cls The class containing the delegated property (unused).
         * @param prop The property itself (unused).
         * @return The current value of the configuration entry.
         */
        operator fun getValue(cls: Any?, prop: KProperty<*>): T = configValue.get()
    }

    /**
     * Companion object responsible for scanning and registering [KConfig] instances.
     * Annotated with `@Scan` to be automatically discovered by Kore's reflection system.
     */
    @Scan
    companion object {
        /**
         * Scans for objects that extend [KConfig] and registers them as mod configurations with NeoForge.
         *
         * This function is invoked by Kore's [ObjectScanner] during mod initialization.
         *
         * @param info The [IModInfo] of the mod being processed.
         * @param container The [ModContainer] of the mod.
         * @param data The [KConfig] instance to be registered.
         */
        @ObjectScanner(KConfig::class)
        fun registerKoreAutoRegistries(info: IModInfo, container: ModContainer, data: KConfig) {
            container.registerConfig(data.type, data.spec)
        }
    }
}

