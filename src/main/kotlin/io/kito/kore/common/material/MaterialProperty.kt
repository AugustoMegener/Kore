package io.kito.kore.common.material

import io.kito.kore.util.Grams

sealed interface MaterialProperty {

    /**
     * Mass of material in *g/ml*.
     */
    val mass: Grams
    /**
     * Burn time by item or 111mb
     */
    val burnTime: Grams

    abstract class Base : MaterialProperty {



        abstract inner class Solid : MaterialProperty by this
        abstract inner class Fluid : MaterialProperty by this
    }


}