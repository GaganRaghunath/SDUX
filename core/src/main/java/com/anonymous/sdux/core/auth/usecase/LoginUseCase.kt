package com.anonymous.sdux.core.auth.usecase

import com.anonymous.sdux.core.auth.repository.AuthRepository
import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.UseCase

data class LoginParams(val email: String, val password: String)

class LoginUseCase(
    private val repository: AuthRepository,
    dispatcherProvider: DispatcherProvider,
) : UseCase<LoginParams, Unit>(dispatcherProvider) {

    override suspend fun execute(params: LoginParams): AppResult<Unit> =
        repository.login(params.email, params.password)
}
