package io.kito.kore.common.data.nbt

import io.kito.kore.common.reflect.ObjectScanner
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

@Scan
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterNBTSerializable(val clazz: KClass<*>) {

    @Scan
    companion object {

        val nbtSerializerRegistry = hashMapOf<KClass<*>, NBTSerializer<*>>()

        @ObjectScanner(NBTSerializer::class)
        fun collectDataScanners(info: IModInfo, container: ModContainer, data: NBTSerializer<*>) {
            data::class.findAnnotation<RegisterNBTSerializable>()?.let { nbtSerializerRegistry[it.clazz] = data }
        }

        @Suppress(UNCHECKED_CAST)
        val <T : Any> KClass<T>.nbtSerializer get() =
            nbtSerializerRegistry.filter { (it, _) -> isSubclassOf(it) }.values.first() as NBTSerializer<T>?
        @Suppress(UNCHECKED_CAST)
        val <T : Any> T.nbtSerializer get() = this::class.nbtSerializer as NBTSerializer<T>?
    }
}
