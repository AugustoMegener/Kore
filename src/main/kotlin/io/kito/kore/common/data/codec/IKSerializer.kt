package io.kito.kore.common.data.codec

import com.mojang.serialization.Codec
import io.kito.kore.util.UNCHECKED_CAST
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

interface IKSerializer<T, C> {

    val fields: List<Pair<Codec<*>, KProperty1<T, *>>>

    val constructor: KFunction<T>

    val codec: C

    @Suppress(UNCHECKED_CAST)
    fun newFrom(values: List<Any>): T {
        var flds = ArrayList(fields).map { it.second }.withIndex()
        val constructorFlds = constructor.parameters.mapNotNull { flds.find { (_, f) -> it.name == f.name } }
            .also       { flds -= it }

        val nonConstructor: List<Any>

        val obj = constructor.call(*constructorFlds
            .mapNotNull { values.withIndex().find { (i, _) -> i == it.index }?.value }
            .also       { nonConstructor = values - it.toSet()              }
            .toTypedArray()
        )

        nonConstructor.withIndex().mapNotNull { (i, v) -> flds.find { (ii, _) -> i == ii }?.let { it.value to v } }
            .forEach { (fld, vl) -> (fld as? KMutableProperty1<T, Any>)?.set(obj, vl) }

        return obj
    }
}