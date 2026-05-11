package com.anonymous.sdux.core.user.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.FlowUseCase
import com.anonymous.sdux.core.user.model.User
import com.anonymous.sdux.core.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val repository: UserRepository,
    dispatcherProvider: DispatcherProvider,
) : FlowUseCase<Unit, User>(dispatcherProvider) {

    override fun execute(params: Unit): Flow<AppResult<User>> = repository.observeCurrentUser()
}
