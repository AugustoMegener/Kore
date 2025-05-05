package io.kito.kore.common.data.codec.stream

import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.snakeCased
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Send(val ord: Int = 0) {

    companion object {

        @Suppress(UNCHECKED_CAST)
        val <T : Any> T.toSendFields get() = this::class.memberProperties.filter { it.hasAnnotation<Send>() }
            .map { it.findAnnotation<Send>()!!.ord to it as KProperty1<T, Any> }.sortedBy { it.first }
    }
}
