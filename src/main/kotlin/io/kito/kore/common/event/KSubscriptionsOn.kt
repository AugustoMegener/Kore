package io.kito.kore.common.event

import net.neoforged.api.distmarker.Dist

/**
 * Annotation used to specify the distribution (client or server) on which event subscriptions
 * within a file or class should be active.
 * This provides a way to control event listener registration abs a broader scope than individual functions.
 *
 * @property dist The [Dist] (client or server) on which the annotated file or class's event subscriptions should be active.
 */
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KSubscriptionsOn(val dist: Dist)

