package com.anonymous.sdux.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private companion object {
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    fun observeToken(): Flow<String?> = dataStore.data.map { it[KEY_ACCESS_TOKEN] }

    fun observeUserId(): Flow<String?> = dataStore.data.map { it[KEY_USER_ID] }

    suspend fun saveToken(token: String, userId: String) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = token
            it[KEY_USER_ID] = userId
        }
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.remove(KEY_ACCESS_TOKEN)
            it.remove(KEY_USER_ID)
        }
    }
}
