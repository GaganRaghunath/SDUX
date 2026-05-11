package com.anonymous.sdux.core.user.model

data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en",
)
