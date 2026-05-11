package com.anonymous.sdux.data.common.di

import com.anonymous.sdux.data.auth.local.SessionDataSource
import com.anonymous.sdux.data.auth.remote.AuthApiService
import com.anonymous.sdux.data.common.network.interceptor.AuthInterceptor
import com.anonymous.sdux.data.common.network.interceptor.buildLoggingInterceptor
import com.anonymous.sdux.data.item.remote.ItemApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.example.com/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(session: SessionDataSource): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(session))
        .addInterceptor(buildLoggingInterceptor())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideItemApiService(retrofit: Retrofit): ItemApiService =
        retrofit.create(ItemApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
}
