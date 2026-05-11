package com.anonymous.sdux.data.item.remote

import com.google.gson.annotations.SerializedName

data class ItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("created_at") val createdAt: Long,
)
