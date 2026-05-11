package com.anonymous.sdux.data.item.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApiService {
    @GET("items")
    suspend fun getItems(): List<ItemDto>

    @GET("items/{id}")
    suspend fun getItemById(@Path("id") id: String): ItemDto
}
