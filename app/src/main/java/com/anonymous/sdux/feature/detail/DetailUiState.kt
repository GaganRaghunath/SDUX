package com.anonymous.sdux.feature.detail

import com.anonymous.sdux.core.item.model.Item

data class DetailUiState(
    val isLoading: Boolean = true,
    val item: Item? = null,
    val errorMessage: String? = null,
)
