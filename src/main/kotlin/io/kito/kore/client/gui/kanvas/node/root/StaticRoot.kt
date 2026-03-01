package io.kito.kore.client.gui.kanvas.node.root

import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Absolute.Companion.px

class StaticRoot(val width: Int, val height: Int) : Root() {
    override var scale: KvsVec = px(width, height)
}