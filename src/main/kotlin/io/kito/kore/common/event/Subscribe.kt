package io.kito.kore.common.event

import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.Dist.CLIENT
import net.neoforged.api.distmarker.Dist.DEDICATED_SERVER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val dist: Array<Dist> = [CLIENT, DEDICATED_SERVER])