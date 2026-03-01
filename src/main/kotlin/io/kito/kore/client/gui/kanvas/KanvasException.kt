package io.kito.kore.client.gui.kanvas

open class KanvasException(val error: Throwable?, val msg: String) : Throwable() {
    constructor(error: Throwable) : this(error, "")
    constructor(msg: String) : this(null, msg)
}