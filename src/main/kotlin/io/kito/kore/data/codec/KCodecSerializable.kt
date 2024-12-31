package io.kito.kore.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import io.kito.kore.data.codec.CodecSource.Companion.codec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.createDynamicCodec
import io.kito.kore.util.snakeCased
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

abstract class KCodecSerializable<T : Any>(clazz: KClass<T>, deserializer: ((List<Any>) -> T)? = null) {

    private val fields = clazz.memberProperties.filter { it.hasAnnotation<Save>() }.map { it.returnType.codec to it }

    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
                        ?: clazz.primaryConstructor!!

    val codec by lazy {
        @Suppress(UNCHECKED_CAST)
        createDynamicCodec<T>(
            fields.map { (it.first as Codec<Any>)
                .fieldOf(it.second.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() } ?: it.second.name.snakeCased())
                .forGetter { o -> it.second.get(o) } }, deserializer ?: ::decode
        )
    }

    @Suppress(UNCHECKED_CAST)
    private fun decode(values: List<Any>): T {
        var flds = ArrayList(fields).map { it.second }.withIndex()
        val constructorFlds = new.parameters.mapNotNull { flds.find { (_, f) -> it.name == f.name } }
                                            .also       { flds -= it }

        val nonConstructor: List<Any>


        val obj = new.call(*constructorFlds.mapNotNull { values.withIndex().find { (i, _) -> i == it.index }?.value }
                                           .also       { nonConstructor = values - it.toSet()              }
                                           .toTypedArray())

        nonConstructor.withIndex().mapNotNull { (i, v) -> flds.find { (ii, _) -> i == ii }?.let { it.value to v } }
            .forEach { (fld, vl) -> (fld as? KMutableProperty1<T, Any>)?.set(obj, vl) }

        return obj
    }

    fun <E> T.encode(ops: DynamicOps<E>): E = codec.encodeStart(ops, this).orThrow
    fun <E> T.encodePartial(ops: DynamicOps<E>): E = codec.encodeStart(ops, this).partialOrThrow

    fun <E> T.safeEncode(ops: DynamicOps<E>) = codec.encodeStart(ops, this).takeIf { it.isSuccess }?.orThrow
    fun <E> T.safeEncodePartial(ops: DynamicOps<E>) =
        codec.encodeStart(ops, this).takeIf { it.hasResultOrPartial() }?.partialOrThrow

    fun <E> decode(ops: DynamicOps<E>, data: E): T = codec.parse(ops, data).orThrow
    fun <E> decodePartial(ops: DynamicOps<E>, data: E): T = codec.parse(ops, data).partialOrThrow

    fun <E> safeDecode(ops: DynamicOps<E>, data: E) = codec.parse(ops, data).takeIf { it.isSuccess }?.orThrow
    fun <E> safeDecodePartial(ops: DynamicOps<E>, data: E) =
        codec.parse(ops, data).takeIf { it.hasResultOrPartial() }?.partialOrThrow
}