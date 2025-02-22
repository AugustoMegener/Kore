package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import io.kito.kore.common.data.codec.CodecSource.Companion.codec
import io.kito.kore.common.reflect.Scan
import kotlin.reflect.KType


@Scan
object CodecSources {
    @CodecSource
    fun intCodec(): PrimitiveCodec<Int> = Codec.INT

    @CodecSource
    fun stringCodec(): PrimitiveCodec<String> = Codec.STRING

    @CodecSource
    fun boolCodec(): PrimitiveCodec<Boolean> = Codec.BOOL

    @CodecSource
    fun listCodec(type: KType) = Codec.list(type.arguments[0].type!!.codec)
}