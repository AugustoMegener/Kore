package io.kito.kore.common.data.codec

/**
 * Annotation used to mark a constructor that should be used for deserialization.
 * When a class has multiple constructors, this annotation helps the deserialization
 * process (e.g., by a [Codec]) to identify the correct constructor to use when
 * reconstructing an object from serialized data.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeserializerConstructor

