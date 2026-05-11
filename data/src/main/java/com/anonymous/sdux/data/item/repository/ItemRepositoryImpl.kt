package com.anonymous.sdux.data.item.repository

import com.anonymous.sdux.core.common.result.AppException
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.item.model.Item
import com.anonymous.sdux.core.item.repository.ItemRepository
import com.anonymous.sdux.data.common.resource.NetworkBoundResource
import com.anonymous.sdux.data.item.local.ItemDao
import com.anonymous.sdux.data.item.mapper.toDomain
import com.anonymous.sdux.data.item.mapper.toEntity
import com.anonymous.sdux.data.item.remote.ItemApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val dao: ItemDao,
    private val api: ItemApiService,
) : ItemRepository {

    override fun observeItems(): Flow<AppResult<List<Item>>> = NetworkBoundResource(
        query = { dao.observeAll() },
        fetch = { api.getItems() },
        saveFetchResult = { dtos ->
            dao.deleteAll()
            dao.insertAll(dtos.map { it.toEntity() })
        },
        mapToResult = { entities -> entities.map { it.toDomain() } },
    ).asFlow()

    override suspend fun getItemById(id: String): AppResult<Item> = try {
        val entity = dao.getById(id)
        if (entity != null) {
            AppResult.Success(entity.toDomain())
        } else {
            AppResult.Error(AppException.DatabaseException("Item not found: $id"))
        }
    } catch (e: Exception) {
        AppResult.Error(AppException.DatabaseException(e.message ?: "DB error", e))
    }

    override suspend fun refreshItems(): AppResult<Unit> = try {
        val dtos = api.getItems()
        dao.deleteAll()
        dao.insertAll(dtos.map { it.toEntity() })
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Error(AppException.NetworkException(e.message ?: "Network error", e))
    }
}
