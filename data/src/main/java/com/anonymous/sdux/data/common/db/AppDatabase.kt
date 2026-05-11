package com.anonymous.sdux.data.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anonymous.sdux.data.common.db.converter.DateTypeConverter
import com.anonymous.sdux.data.item.local.ItemDao
import com.anonymous.sdux.data.item.local.ItemEntity
import com.anonymous.sdux.data.user.local.UserDao
import com.anonymous.sdux.data.user.local.UserEntity

@Database(
    entities = [ItemEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao
}
