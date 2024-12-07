package io.kito.kore.common.registry

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class Register(val clazz: KClass<*>, val registry: String = "")