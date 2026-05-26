package com.example.kotmod6.presentation

import com.example.kotmod6.domain.model.Photo

sealed interface CatalogState {
    data object Loading : CatalogState
    data class Success(val photos: List<Photo>) : CatalogState
    data class Error(val message: String) : CatalogState
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data object Saving : DownloadState
    data class Done(val message: String) : DownloadState
    data class Error(val message: String) : DownloadState
}
