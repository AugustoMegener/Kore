package io.kito.kore.common.data

import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.snakeCased
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Save(val id: String = "") {

    companion object {

        @Suppress(UNCHECKED_CAST)
        val <T : Any> T.saveFields get() = this::class.memberProperties.filter { it.hasAnnotation<Save>() }
            .map { (it.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                ?: it.name.snakeCased()) to it as KProperty1<T, Any> }
    }
}
