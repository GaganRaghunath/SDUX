package com.anonymous.sdux.feature.home

import com.anonymous.sdux.common.BaseViewModel
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.item.usecase.GetItemsUseCase
import com.anonymous.sdux.core.item.usecase.RefreshItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val refreshItemsUseCase: RefreshItemsUseCase,
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    init {
        observeItems()
    }

    fun onEvent(event: HomeIntent) {
        when (event) {
            is HomeIntent.ItemClicked -> emitEvent(HomeUiEvent.NavigateToDetail(event.itemId))
            is HomeIntent.RefreshRequested -> refresh()
            is HomeIntent.SettingsClicked -> emitEvent(HomeUiEvent.NavigateToSettings)
        }
    }

    private fun observeItems() {
        launch {
            getItemsUseCase(Unit).collect { result ->
                when (result) {
                    is AppResult.Loading -> updateState { copy(isLoading = true) }
                    is AppResult.Success -> updateState { copy(isLoading = false, items = result.data, errorMessage = null) }
                    is AppResult.Error -> updateState { copy(isLoading = false, errorMessage = result.exception.message) }
                }
            }
        }
    }

    private fun refresh() {
        launch {
            when (val result = refreshItemsUseCase(Unit)) {
                is AppResult.Error -> emitEvent(HomeUiEvent.ShowSnackbar(result.exception.message ?: "Refresh failed"))
                else -> Unit
            }
        }
    }
}

sealed class HomeIntent {
    data class ItemClicked(val itemId: String) : HomeIntent()
    object RefreshRequested : HomeIntent()
    object SettingsClicked : HomeIntent()
}
