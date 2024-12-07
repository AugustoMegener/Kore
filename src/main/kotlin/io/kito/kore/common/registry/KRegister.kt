package io.kito.kore.common.registry

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class KRegister(val registry: KClass<*> = Nothing::class, val name: String = "") {
}