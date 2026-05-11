package com.anonymous.sdux.data.item.mapper

import com.anonymous.sdux.core.item.model.Item
import com.anonymous.sdux.data.item.local.ItemEntity
import com.anonymous.sdux.data.item.remote.ItemDto

fun ItemDto.toDomain(): Item = Item(id, title, description, imageUrl, createdAt)

fun ItemEntity.toDomain(): Item = Item(id, title, description, imageUrl, createdAt)

fun Item.toEntity(): ItemEntity = ItemEntity(id, title, description, imageUrl, createdAt)

fun ItemDto.toEntity(): ItemEntity = ItemEntity(id, title, description, imageUrl, createdAt)
