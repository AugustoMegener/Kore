package io.kito.kore.client.gui.kanvas.node.root

import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.client.gui.kanvas.transform.KvsVec.Function.Companion.fn

class FlexRoot(val width: () -> Int, val height: () -> Int) : Root() {
    override var scale: KvsVec = fn({ width() }, { height() })
}