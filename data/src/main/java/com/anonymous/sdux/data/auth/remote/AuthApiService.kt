package com.anonymous.sdux.data.auth.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body refreshToken: String): TokenResponse

    @POST("auth/logout")
    suspend fun logout()
}
