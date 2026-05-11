package com.anonymous.sdux.core.common.usecase

abstract class SyncUseCase<in P, out R> {
    operator fun invoke(params: P): R = execute(params)

    protected abstract fun execute(params: P): R
}
