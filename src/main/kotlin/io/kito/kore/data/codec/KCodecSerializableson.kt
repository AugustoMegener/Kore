package io.kito.kore.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.createDynamicCodec
import io.kito.kore.util.snakeCased
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class KCodecSerializableson<T : Any>(val new: () -> T) {

    private val fields by lazy { new().fieldsWithNames }

    val codec by lazy {
        @Suppress(UNCHECKED_CAST)
        createDynamicCodec<T>(
            fields.mapIndexed { i, (n, it) -> (it.codec as Codec<Any>)
                .fieldOf(it.name ?: n).forGetter { obj -> obj.fields[i].value } })
        { new().also { obj -> obj.fields.zip(it).forEach { (f, d) -> f.unsafeSetValue(d) } } }
    }

    private val T.fields get() =
        javaClass.kotlin.memberProperties.mapNotNull { it.also { it.isAccessible = true }.getDelegate(this) }
                                         .filterIsInstance<Field<*>>()

    @Suppress(UNCHECKED_CAST)
    private val T.fieldsWithNames get() =
        javaClass.kotlin.memberProperties
            .mapNotNull { (it.also { it.isAccessible = true }.getDelegate(this) as? Field<*>)
                ?.let { d -> it.name.snakeCased() to d } }

    operator fun invoke(builder: T.() -> Unit) = new().apply(builder)

    fun <E> T.encode(ops: DynamicOps<E>) = codec.encodeStart(ops, this).orThrow
    fun <E> T.encodePartial(ops: DynamicOps<E>) = codec.encodeStart(ops, this).partialOrThrow

    fun <E> T.safeEncode(ops: DynamicOps<E>) = codec.encodeStart(ops, this).takeIf { it.isSuccess }?.orThrow
    fun <E> T.safeEncodePartial(ops: DynamicOps<E>) =
        codec.encodeStart(ops, this).takeIf { it.hasResultOrPartial() }?.partialOrThrow

    fun <E> decode(ops: DynamicOps<E>, data: E) = codec.parse(ops, data).orThrow
    fun <E> decodePartial(ops: DynamicOps<E>, data: E) = codec.parse(ops, data).partialOrThrow

    fun <E> safeDecode(ops: DynamicOps<E>, data: E) = codec.parse(ops, data).takeIf { it.isSuccess }?.orThrow
    fun <E> safeDecodePartial(ops: DynamicOps<E>, data: E) =
        codec.parse(ops, data).takeIf { it.hasResultOrPartial() }?.partialOrThrow

    inner class Field<T : Any>(val codec: Codec<T>, val name: String? = null) {
        @Internal internal lateinit var value: T

        operator fun getValue(obj: Any?, field: KProperty<*>) = value
        operator fun setValue(obj: Any?, field: KProperty<*>, new: T) { value = new }

        infix fun default(defaultValue: T) = also { value = defaultValue }

         @Suppress(UNCHECKED_CAST)
         @Internal internal fun unsafeSetValue(new: Any) { value = new as T }
    }
}