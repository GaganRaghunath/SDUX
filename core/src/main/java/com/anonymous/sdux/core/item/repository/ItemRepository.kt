package com.anonymous.sdux.core.item.repository

import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.item.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun observeItems(): Flow<AppResult<List<Item>>>
    suspend fun getItemById(id: String): AppResult<Item>
    suspend fun refreshItems(): AppResult<Unit>
}
