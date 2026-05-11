package com.anonymous.sdux.di

import com.anonymous.sdux.core.auth.repository.AuthRepository
import com.anonymous.sdux.core.auth.usecase.LoginUseCase
import com.anonymous.sdux.core.auth.usecase.ObserveAuthStateUseCase
import com.anonymous.sdux.core.common.dispatcher.DefaultDispatcherProvider
import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.item.repository.ItemRepository
import com.anonymous.sdux.core.item.usecase.GetItemByIdUseCase
import com.anonymous.sdux.core.item.usecase.GetItemsUseCase
import com.anonymous.sdux.core.item.usecase.RefreshItemsUseCase
import com.anonymous.sdux.core.user.repository.UserRepository
import com.anonymous.sdux.core.user.usecase.GetCurrentUserUseCase
import com.anonymous.sdux.core.user.usecase.LogoutUseCase
import com.anonymous.sdux.core.user.usecase.UpdateUserPreferencesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    fun provideGetItemsUseCase(repo: ItemRepository, dp: DispatcherProvider) =
        GetItemsUseCase(repo, dp)

    @Provides
    fun provideGetItemByIdUseCase(repo: ItemRepository, dp: DispatcherProvider) =
        GetItemByIdUseCase(repo, dp)

    @Provides
    fun provideRefreshItemsUseCase(repo: ItemRepository, dp: DispatcherProvider) =
        RefreshItemsUseCase(repo, dp)

    @Provides
    fun provideGetCurrentUserUseCase(repo: UserRepository, dp: DispatcherProvider) =
        GetCurrentUserUseCase(repo, dp)

    @Provides
    fun provideUpdateUserPreferencesUseCase(repo: UserRepository, dp: DispatcherProvider) =
        UpdateUserPreferencesUseCase(repo, dp)

    @Provides
    fun provideLogoutUseCase(repo: UserRepository, dp: DispatcherProvider) =
        LogoutUseCase(repo, dp)

    @Provides
    fun provideLoginUseCase(repo: AuthRepository, dp: DispatcherProvider) =
        LoginUseCase(repo, dp)

    @Provides
    fun provideObserveAuthStateUseCase(repo: AuthRepository, dp: DispatcherProvider) =
        ObserveAuthStateUseCase(repo, dp)
}
