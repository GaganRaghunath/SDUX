package com.anonymous.sdux.core.auth.model

sealed class AuthState {
    data class Authenticated(val userId: String) : AuthState()
    object Unauthenticated : AuthState()
}
