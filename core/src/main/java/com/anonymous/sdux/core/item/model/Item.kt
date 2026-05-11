package com.anonymous.sdux.core.item.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val createdAt: Long,
)
