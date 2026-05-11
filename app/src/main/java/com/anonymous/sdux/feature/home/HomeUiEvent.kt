package com.anonymous.sdux.feature.home

sealed class HomeUiEvent {
    data class NavigateToDetail(val itemId: String) : HomeUiEvent()
    data class ShowSnackbar(val message: String) : HomeUiEvent()
    object NavigateToSettings : HomeUiEvent()
}
