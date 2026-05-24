package com.anonymous.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Bind repository interfaces to their implementations:
    //
    // @Binds
    // abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
