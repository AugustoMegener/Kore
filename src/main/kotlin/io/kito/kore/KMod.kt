package io.kito.kore

/**
 * Annotation used to mark the main entry point function of a Kore mod.
 * Functions annotated with `@KMod` are automatically discovered and executed by the Kore framework
 * during mod initialization.
 *
 * @property id An optional identifier for the mod. If not provided, the mod ID might be inferred
 *              from the package name or other configuration.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class KMod(val id: String = "")

