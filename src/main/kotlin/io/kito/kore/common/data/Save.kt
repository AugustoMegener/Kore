package io.kito.kore.common.data

import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.snakeCased
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Annotation used to mark properties that should be saved or serialized.
 * When applied to a property, it indicates that the property's value should be persisted
 * during data saving operations, typically to NBT or other data formats.
 *
 * @property id An optional identifier for the saved property. If not provided, the property's
 *              name will be converted to snake_case and used as the identifier.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Save(val id: String = "") {

    /**
     * Companion object providing utility functions related to the [Save] annotation.
     */
    companion object {

        /**
         * An extension property that returns a list of properties annotated with [Save] from an object.
         * Each item in the list is a pair: the first element is the ID (or snake_cased name) of the property,
         * and the second element is the [KProperty1] representing the property itself.
         *
         * This allows for easy iteration and access to all savable fields of an object.
         *
         * @param T The type of the object.
         * @receiver The object from which to retrieve savable fields.
         * @return A list of pairs, each containing the save ID and the [KProperty1] of a savable field.
         */
        @Suppress(UNCHECKED_CAST)
        val <T : Any> T.saveFields get() = this::class.memberProperties.filter { it.hasAnnotation<Save>() }
            .map { (it.findAnnotation<Save>()!!.id.takeIf { s -> s.isNotEmpty() }
                ?: it.name.snakeCased()) to it as KProperty1<T, Any> }
    }
}

