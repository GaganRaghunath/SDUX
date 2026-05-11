package com.anonymous.sdux.data.common.di

import com.anonymous.sdux.core.auth.repository.AuthRepository
import com.anonymous.sdux.core.item.repository.ItemRepository
import com.anonymous.sdux.core.user.repository.UserRepository
import com.anonymous.sdux.data.auth.repository.AuthRepositoryImpl
import com.anonymous.sdux.data.item.repository.ItemRepositoryImpl
import com.anonymous.sdux.data.user.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
