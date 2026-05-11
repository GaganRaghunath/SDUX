package com.anonymous.sdux.feature.settings

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en",
)
