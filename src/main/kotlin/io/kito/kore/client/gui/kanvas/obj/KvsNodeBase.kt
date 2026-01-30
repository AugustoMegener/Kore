package io.kito.kore.client.gui.kanvas.obj

import io.kito.kore.util.UNCHECKED_CAST

abstract class KvsNodeBase : KvsNode {

    abstract override val parent: KvsNode
    override var children = listOf<KvsNode>()

    @Suppress(UNCHECKED_CAST)
    override fun addChild(obj: KvsNode) { children += obj }
}