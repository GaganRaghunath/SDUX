package com.anonymous.sdux.feature.detail

sealed class DetailUiEvent {
    object NavigateBack : DetailUiEvent()
    data class ShowSnackbar(val message: String) : DetailUiEvent()
}
