package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.Photo
import com.example.kotmod6.domain.repository.PhotoRepository

class DownloadPhotoUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo): ByteArray = repository.loadPhotoFile(photo)
}
