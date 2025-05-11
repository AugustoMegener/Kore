package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.createDynamicCodec
import io.kito.kore.util.snakeCased
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

interface IKCodecSerializer<T : Any> : IKSerializer<T, Codec<T>> {
    val clazz: KClass<T>

    override val fields
        get() = clazz.memberProperties.filter { it.hasAnnotation<Save>() }.map { it.returnType.codec to it }

    override val constructor
        get() = clazz.constructors.find { it.hasAnnotation<DeserializerConstructor>() } ?: clazz.primaryConstructor!!


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


    companion object {
        fun <T : Any> IKCodecSerializer<T>.lazyCodec() =
            lazy {
                @Suppress(UNCHECKED_CAST)(
                        createDynamicCodec<T>(
                            fields.map {
                                (it.first as Codec<Any>)
                                    .fieldOf(it.second.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                                        ?: it.second.name.snakeCased())
                                    .forGetter { o -> it.second.get(o) }
                            }, ::newFrom
                        )
                )
            }
    }
}
