package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.Photo
import com.example.kotmod6.domain.repository.PhotoRepository

class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): List<Photo> = repository.loadPhotos()
}
