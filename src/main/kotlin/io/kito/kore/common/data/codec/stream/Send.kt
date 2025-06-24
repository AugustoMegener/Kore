package io.kito.kore.common.data.codec.stream

import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.snakeCased
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Annotation used to mark properties that should be included in network packet serialization.
 * When applied to a property, it indicates that the property's value should be sent over the network.
 *
 * @property ord The order in which this property should be serialized. Lower values are serialized first.
 *              This is crucial for consistent and correct deserialization on the receiving end.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Send(val ord: Int = 0) {

    /**
     * Companion object providing utility functions related to the [Send] annotation.
     */
    companion object {

        /**
         * An extension property that returns a list of properties annotated with [Send] from an object,
         * sorted by their `ord` value.
         * Each item in the list is a pair: the order (`ord`) of the property,
         * and the [KProperty1] representing the property itself.
         *
         * This allows for easy iteration and access to all sendable fields of an object in a defined order.
         *
         * @param T The type of the object.
         * @receiver The object from which to retrieve sendable fields.
         * @return A list of pairs, each containing the order and the [KProperty1] of a sendable field.
         */
        @Suppress(UNCHECKED_CAST)
        val <T : Any> T.toSendFields get() = this::class.memberProperties.filter { it.hasAnnotation<Send>() }
            .map { it.findAnnotation<Send>()!!.ord to it as KProperty1<T, Any> }.sortedBy { it.first }
    }
}

