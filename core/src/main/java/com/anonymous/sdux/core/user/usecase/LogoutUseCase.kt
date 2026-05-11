package com.anonymous.sdux.core.user.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.UseCase
import com.anonymous.sdux.core.user.repository.UserRepository

class LogoutUseCase(
    private val repository: UserRepository,
    dispatcherProvider: DispatcherProvider,
) : UseCase<Unit, Unit>(dispatcherProvider) {

    override suspend fun execute(params: Unit): AppResult<Unit> = repository.logout()
}
