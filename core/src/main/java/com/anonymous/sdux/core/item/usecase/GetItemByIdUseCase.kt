package com.anonymous.sdux.core.item.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.UseCase
import com.anonymous.sdux.core.item.model.Item
import com.anonymous.sdux.core.item.repository.ItemRepository

class GetItemByIdUseCase(
    private val repository: ItemRepository,
    dispatcherProvider: DispatcherProvider,
) : UseCase<String, Item>(dispatcherProvider) {

    override suspend fun execute(params: String): AppResult<Item> = repository.getItemById(params)
}
