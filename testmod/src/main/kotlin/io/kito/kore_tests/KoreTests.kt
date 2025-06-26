package io.kito.kore_tests

import io.kito.kore.KMod
import io.kito.kore.common.datagen.DataGenHelper
import io.kito.kore.common.reflect.Scan

/**
 * The main entry point for the Kore Tests mod.
 * This function is annotated with `@KMod`, indicating that it's a Kore Mod entry point.
 * It is responsible for initializing the mod.
 */
@KMod
fun init() {
    // Mod initialization logic can be added here.
    // This function is called by the Kore framework during mod loading.
}

/**
 * DataGenerator object for handling data generation within the Kore Tests mod.
 * Annotated with `@Scan`, indicating that Kore should scan this object for data generation providers.
 * It extends `DataGenHelper` and uses the mod's ID for data generation.
 */
@Scan
object DataGenerator : DataGenHelper(ID)

