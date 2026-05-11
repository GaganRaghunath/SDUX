package com.anonymous.sdux.feature.home

import com.anonymous.sdux.core.item.model.Item

data class HomeUiState(
    val isLoading: Boolean = true,
    val items: List<Item> = emptyList(),
    val errorMessage: String? = null,
)
