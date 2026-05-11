package com.anonymous.sdux.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.anonymous.sdux.common.BaseViewModel
import com.anonymous.sdux.core.common.result.AppResult
import com.anonymous.sdux.core.item.usecase.GetItemByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DetailUiState, DetailUiEvent>(DetailUiState()) {

    init {
        val itemId = savedStateHandle.get<String>("itemId") ?: return
        loadItem(itemId)
    }

    private fun loadItem(itemId: String) {
        launch {
            when (val result = getItemByIdUseCase(itemId)) {
                is AppResult.Success -> updateState { copy(isLoading = false, item = result.data) }
                is AppResult.Error -> updateState { copy(isLoading = false, errorMessage = result.exception.message) }
                is AppResult.Loading -> updateState { copy(isLoading = true) }
            }
        }
    }
}
