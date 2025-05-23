package io.kito.kore.common.data.codec.stream

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import com.mojang.serialization.RecordBuilder
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.CodecSource
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.common.data.codec.DeserializerConstructor
import io.kito.kore.common.data.codec.stream.StreamCodecSource.Companion.streamCodec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicMapCodec
import io.kito.kore.util.minecraft.createDynamicStreamCodec
import io.kito.kore.util.snakeCased
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

open class KStreamMapCodecSerializer<B: ByteBuf, T : Any>(clazz: KClass<T>, byteBuf: KClass<B>, deserializer: ((List<Any>) -> T)? = null) {

    private val fields by
        lazy { clazz.memberProperties.filter { it.hasAnnotation<Save>() }.map { it.returnType.codec to it } }

    private val streamFields by lazy {
        clazz.memberProperties
            .filter { it.hasAnnotation<Save>() }
            .map { it.returnType.streamCodec to it }
    }

    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
        ?: clazz.primaryConstructor!!

    init {
        CodecSource.sources[clazz] = { _ -> mapCodec.codec() }
        StreamCodecSource.sources += byteBuf to { _ -> streamCodec }
    }

    val mapCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicMapCodec<T>(
            fields.map {
                (it.first as Codec<Any>)
                    .fieldOf(it.second.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                        ?: it.second.name.snakeCased())
                    .forGetter { o -> it.second.get(o) }
            }, deserializer ?: ::mapDecode
        ))
    }

    @Suppress(UNCHECKED_CAST)
    private fun mapDecode(values: List<Any>): T {
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

    fun <E> RecordBuilder<E>.encode(ops: DynamicOps<E>, data: T): RecordBuilder<E> =
        mapCodec.encode<E>(data, ops, this)

    fun <E> MapLike<E>.decode(ops: DynamicOps<E>): T =
        mapCodec.decode(ops, this).orThrow
    fun <E> MapLike<E>.decodePartial(ops: DynamicOps<E>): T =
        mapCodec.decode(ops, this).partialOrThrow

    fun <E> MapLike<E>.safeDecode(ops: DynamicOps<E>): T? =
        mapCodec.decode(ops, this).result().getOrNull()


    val streamCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicStreamCodec(
            streamFields.map { (it.first as StreamCodec<B, Any>) to { o -> it.second.get(o) } },
            deserializer ?: ::streamDecode
        ))
    }

    @Suppress(UNCHECKED_CAST)
    private fun streamDecode(values: List<Any>): T {
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

    fun B.put(data: T) { streamCodec.encode(this, data) }

    fun B.read(): T = streamCodec.decode(this)
}