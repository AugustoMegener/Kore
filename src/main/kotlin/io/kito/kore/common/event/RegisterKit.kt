package io.kito.kore.common.event

/**
 * Annotation used to mark properties that should be automatically registered as part of a "kit".
 * The exact meaning of "kit" and how properties marked with this annotation are handled
 * depends on the specific implementation that processes this annotation.
 *
 * This annotation serves as a marker for automatic discovery and registration of components.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterKit

