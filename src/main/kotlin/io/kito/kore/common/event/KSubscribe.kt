package io.kito.kore.common.event

import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.Dist.CLIENT
import net.neoforged.api.distmarker.Dist.DEDICATED_SERVER

/**
 * Annotation used to mark functions as event subscribers in Kore.
 * Functions annotated with [KSubscribe] will be automatically discovered by the [EventScanner]
 * and registered with the appropriate NeoForge event bus.
 *
 * This annotation simplifies event handling by removing the need for manual event bus registration.
 *
 * @property dist An array of [Dist] values indicating on which side(s) (client, dedicated server) this event subscriber should be active.
 *                  By default, the event subscriber will be active on both client and dedicated server.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KSubscribe(val dist: Array<Dist> = [CLIENT, DEDICATED_SERVER])

