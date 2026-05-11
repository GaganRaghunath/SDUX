package com.anonymous.sdux.data.user.repository

import com.anonymous.sdux.core.common.result.AppException
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.user.model.User
import com.anonymous.sdux.core.user.model.UserPreferences
import com.anonymous.sdux.core.user.repository.UserRepository
import com.anonymous.sdux.data.user.datastore.UserPreferencesDataSource
import com.anonymous.sdux.data.user.local.UserDao
import com.anonymous.sdux.data.user.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val prefsDataSource: UserPreferencesDataSource,
) : UserRepository {

    override fun observeCurrentUser(): Flow<AppResult<User>> = dao.observeCurrentUser().map { entity ->
        if (entity != null) AppResult.Success(entity.toDomain())
        else AppResult.Error(AppException.DatabaseException("No user logged in"))
    }

    override suspend fun updatePreferences(preferences: UserPreferences): AppResult<Unit> = try {
        prefsDataSource.updateDarkTheme(preferences.isDarkTheme)
        prefsDataSource.updateNotifications(preferences.notificationsEnabled)
        prefsDataSource.updateLanguage(preferences.language)
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Error(AppException.UnknownException(e.message ?: "Prefs error", e))
    }

    override suspend fun logout(): AppResult<Unit> = try {
        dao.deleteAll()
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Error(AppException.DatabaseException(e.message ?: "Logout error", e))
    }
}
