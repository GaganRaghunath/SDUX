package com.anonymous.sdux.data.user.mapper

import com.anonymous.sdux.core.user.model.User
import com.anonymous.sdux.data.user.local.UserEntity

fun UserEntity.toDomain(): User = User(id, name, email, avatarUrl)

fun User.toEntity(): UserEntity = UserEntity(id, name, email, avatarUrl)
