package com.anonymous.sdux.data.user.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anonymous.sdux.core.user.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            isDarkTheme = prefs[KEY_DARK_THEME] ?: false,
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
            language = prefs[KEY_LANGUAGE] ?: "en",
        )
    }

    suspend fun updateDarkTheme(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_THEME] = enabled }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }
}
