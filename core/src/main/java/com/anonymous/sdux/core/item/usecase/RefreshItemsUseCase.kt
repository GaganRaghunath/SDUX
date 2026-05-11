package com.anonymous.sdux.core.item.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.common.usecase.UseCase
import com.anonymous.sdux.core.item.repository.ItemRepository

class RefreshItemsUseCase(
    private val repository: ItemRepository,
    dispatcherProvider: DispatcherProvider,
) : UseCase<Unit, Unit>(dispatcherProvider) {

    override suspend fun execute(params: Unit): AppResult<Unit> = repository.refreshItems()
}
