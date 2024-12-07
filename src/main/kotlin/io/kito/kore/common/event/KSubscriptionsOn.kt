package io.kito.kore.common.event

import net.neoforged.api.distmarker.Dist

@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KSubscriptionsOn(val dist: Dist)