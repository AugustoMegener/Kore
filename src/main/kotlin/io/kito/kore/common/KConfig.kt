package io.kito.kore.common

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KProperty

abstract class KConfig(val type: ModConfig.Type) {

    private val values = arrayListOf<Value<*>>()

    val spec: ModConfigSpec by lazy { ModConfigSpec.Builder().configure(::config).value }

    fun config(builder: ModConfigSpec.Builder) : KConfig {
        values.forEach {
            it.configure(builder)
        }
        return this
    }

    inner class Value<T>(private val getter: ModConfigSpec.Builder.() -> ModConfigSpec.ConfigValue<T>) {

        private lateinit var configValue: ModConfigSpec.ConfigValue<T>

        init { values += this }

        fun configure(builder: ModConfigSpec.Builder) { configValue = getter(builder) }

        operator fun getValue(cls: Any?, prop: KProperty<*>): T = configValue.get()
    }

    @Scan
    companion object {
        @ObjectScanner(KConfig::class)
        fun registerKoreAutoRegistries(info: IModInfo, container: ModContainer, data: KConfig) {
            container.registerConfig(data.type, data.spec)
        }
    }
}