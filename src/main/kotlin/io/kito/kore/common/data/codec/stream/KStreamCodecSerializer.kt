package io.kito.kore.common.data.codec.stream

import io.kito.kore.common.data.codec.DeserializerConstructor
import io.kito.kore.common.data.codec.stream.StreamCodecSource.Companion.streamCodec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicStreamCodec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

open class KStreamCodecSerializer<B: ByteBuf, T : Any>(clazz: KClass<T>, byteBuf: KClass<B>, deserializer: ((List<Any>) -> T)? = null) {

    private val fields by lazy {
        clazz.memberProperties
            .filter { it.hasAnnotation<Send>() }
            .map { it.returnType.streamCodec to it }
            .sortedBy { it.second.findAnnotation<Send>()!!.ord }
    }

    private val new = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() }
                        ?: clazz.primaryConstructor!!

    init { StreamCodecSource.Companion.sources += byteBuf to { _ -> streamCodec } }

    val streamCodec by lazy {
        @Suppress(UNCHECKED_CAST)
        (createDynamicStreamCodec(
            fields.map { (it.first as StreamCodec<B, Any>) to { o -> it.second.get(o) } },
            deserializer ?: ::decode
        ))
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

    fun B.put(data: T) { streamCodec.encode(this, data) }

    fun B.read(): T = streamCodec.decode(this)
}