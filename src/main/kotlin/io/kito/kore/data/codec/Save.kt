package io.kito.kore.data.codec

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Save(val id: String = "")
