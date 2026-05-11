package com.anonymous.sdux.core.user.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.UseCase
import com.anonymous.sdux.core.user.model.UserPreferences
import com.anonymous.sdux.core.user.repository.UserRepository

class UpdateUserPreferencesUseCase(
    private val repository: UserRepository,
    dispatcherProvider: DispatcherProvider,
) : UseCase<UserPreferences, Unit>(dispatcherProvider) {

    override suspend fun execute(params: UserPreferences): AppResult<Unit> =
        repository.updatePreferences(params)
}
