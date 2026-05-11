package com.anonymous.sdux.data.common.di

import android.content.Context
import androidx.room.Room
import com.anonymous.sdux.data.common.db.AppDatabase
import com.anonymous.sdux.data.item.local.ItemDao
import com.anonymous.sdux.data.user.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "sdux.db").build()

    @Provides
    fun provideItemDao(db: AppDatabase): ItemDao = db.itemDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
