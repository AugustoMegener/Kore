package io.kito.kore.client.gui.kanvas.transform

import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.transform.KvsVec.FitChildren
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Function.Companion.fn

data class KvsTransform(val pos: KvsVec, val scale: KvsVec) {

    fun pos(block: (KvsVec) -> KvsVec) = KvsTransform(block(pos), scale)
    fun scale(block: (KvsVec) -> KvsVec) = KvsTransform(pos, block(scale))

    companion object {

        fun margin(size: KvsVec) =
            KvsTransform(size, fn({ parent.width - size.x(this) * 2 }, { parent.height - size.y(this) * 2 }))

        fun padding(size: KvsVec) =
            KvsTransform(size, fn({ FitChildren.x(this) + size.x(this) }, { FitChildren.y(this) + size.y(this) }))
    }
}