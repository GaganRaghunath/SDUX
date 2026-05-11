package com.anonymous.sdux.core.item.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.FlowUseCase
import com.anonymous.sdux.core.item.model.Item
import com.anonymous.sdux.core.item.repository.ItemRepository
import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(
    private val repository: ItemRepository,
    dispatcherProvider: DispatcherProvider,
) : FlowUseCase<Unit, List<Item>>(dispatcherProvider) {

    override fun execute(params: Unit): Flow<AppResult<List<Item>>> = repository.observeItems()
}
