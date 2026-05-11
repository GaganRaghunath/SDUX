package com.anonymous.sdux.data.auth.mapper

import com.anonymous.sdux.core.auth.model.AuthState
import com.anonymous.sdux.data.auth.remote.TokenResponse

fun TokenResponse.toAuthState(): AuthState.Authenticated = AuthState.Authenticated(userId)
