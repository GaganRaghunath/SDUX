package com.anonymous.sdux.feature.settings

import com.anonymous.sdux.common.BaseViewModel
import com.anonymous.sdux.core.user.model.UserPreferences
import com.anonymous.sdux.core.user.usecase.UpdateUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updatePreferencesUseCase: UpdateUserPreferencesUseCase,
) : BaseViewModel<SettingsUiState, Unit>(SettingsUiState()) {

    fun toggleDarkTheme(enabled: Boolean) {
        updateState { copy(isDarkTheme = enabled) }
        savePreferences()
    }

    fun toggleNotifications(enabled: Boolean) {
        updateState { copy(notificationsEnabled = enabled) }
        savePreferences()
    }

    private fun savePreferences() {
        launch {
            updatePreferencesUseCase(
                UserPreferences(
                    isDarkTheme = uiState.value.isDarkTheme,
                    notificationsEnabled = uiState.value.notificationsEnabled,
                    language = uiState.value.language,
                )
            )
        }
    }
}
