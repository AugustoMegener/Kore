package io.kito.kore;

import io.kito.kore.Kore.ID
import io.kito.kore.reflect.ClassScanner.Companion.scanClasses
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager

//import net.neoforged.fml.common.Mod as b



@Mod(ID)
object Kore {

    const val ID = "kore"
    val logger = LogManager.getLogger(ID)!!

    init {
        scanClasses()
    }
}