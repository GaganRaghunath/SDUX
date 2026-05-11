package com.anonymous.sdux.core.auth.repository

import com.anonymous.sdux.core.auth.model.AuthState
import com.anonymous.sdux.core.common.result.AppResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<AuthState>
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
}
