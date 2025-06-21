package io.kito.kore_tests.common.registry

import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.registry.DataComponentTypeRegister
import io.kito.kore_tests.ID

/**
 * Registers custom data component types for the Kore Tests mod.
 * Annotated with `@Scan` to be automatically discovered by Kore for data component registration.
 * Extends `DataComponentTypeRegister` with the mod ID, providing a mechanism for defining custom data components.
 */
@Scan
object DataComponents : DataComponentTypeRegister(ID)

