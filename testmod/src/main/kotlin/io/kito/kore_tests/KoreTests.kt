package io.kito.kore_tests

import io.kito.kore.ksp.KMod
import org.apache.logging.log4j.LogManager


val logger = LogManager.getLogger(ID)!!

@KMod
fun init() {
    logger.warn("ESQUECE! AS IRMÃ TÁ ESTOURADA!")
}