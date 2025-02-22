package io.kito.kore.common.registry

sealed class RegistrationCtx<T> {
    abstract val  ctx: T
    abstract val name: String

    class Simple
}