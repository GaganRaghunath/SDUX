package com.anonymous.sdux.core.user.repository

import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.user.model.User
import com.anonymous.sdux.core.user.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<AppResult<User>>
    suspend fun updatePreferences(preferences: UserPreferences): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
}
