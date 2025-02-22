package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CodecSource {

    @Scan
    companion object : FunScanner<Codec<*>> {

        internal val sources = hashMapOf<KClass<*>, (KType) -> Codec<*>>()

        override val bound      = globalBound
        override val annotation = CodecSource::class
        override val returnType = Codec      ::class

        override fun validateParameters(parms: List<KParameter>) =
            (parms.size == 1) || (parms.size == 2 && parms[1].type.jvmErasure == KType::class)

        override fun use(info: IModInfo, container: ModContainer, data: KFunction<Codec<*>>) {
            val obj = data.javaMethod!!.declaringClass.kotlin.objectInstance!!

            sources[data.returnType.arguments.first().type!!.jvmErasure] =
                when(data.parameters.size) { 1 -> { { data.call(obj)   } }
                                             2 -> { { data.call(obj, it) } }
                                             else -> throw IllegalStateException("Invalid function parameters") }
        }

        val KType.codecOrNull get() = sources[jvmErasure]?.let { it(this) }

        val KType.codec get() = codecOrNull ?: throw IllegalStateException("No serializer source for $jvmErasure")

        @Suppress(UNCHECKED_CAST)
        fun <T> KType.codec() = codec as Codec<T>
    }
}
