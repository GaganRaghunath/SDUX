package com.anonymous.sdux.data.auth.repository

import com.anonymous.sdux.core.auth.model.AuthState
import com.anonymous.sdux.core.auth.repository.AuthRepository
import com.anonymous.sdux.core.common.result.AppException
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.data.auth.local.SessionDataSource
import com.anonymous.sdux.data.auth.remote.AuthApiService
import com.anonymous.sdux.data.auth.remote.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val session: SessionDataSource,
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> = session.observeToken().map { token ->
        if (token != null) AuthState.Authenticated(token) else AuthState.Unauthenticated
    }

    override suspend fun login(email: String, password: String): AppResult<Unit> = try {
        val response = api.login(LoginRequest(email, password))
        session.saveToken(response.accessToken, response.userId)
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Error(AppException.NetworkException(e.message ?: "Login failed", e))
    }

    override suspend fun logout(): AppResult<Unit> = try {
        api.logout()
        session.clearToken()
        AppResult.Success(Unit)
    } catch (e: Exception) {
        session.clearToken()
        AppResult.Success(Unit)
    }
}
