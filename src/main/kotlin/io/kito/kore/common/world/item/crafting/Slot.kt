package io.kito.kore.common.world.item.crafting

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Slot(val slot: Int)