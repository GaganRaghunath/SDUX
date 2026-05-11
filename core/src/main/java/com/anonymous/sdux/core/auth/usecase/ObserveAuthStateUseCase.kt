package com.anonymous.sdux.core.auth.usecase

import com.anonymous.sdux.core.auth.model.AuthState
import com.anonymous.sdux.core.auth.repository.AuthRepository
import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveAuthStateUseCase(
    private val repository: AuthRepository,
    dispatcherProvider: DispatcherProvider,
) : FlowUseCase<Unit, AuthState>(dispatcherProvider) {

    override fun execute(params: Unit): Flow<AppResult<AuthState>> =
        repository.observeAuthState().map { AppResult.Success(it) }
}
