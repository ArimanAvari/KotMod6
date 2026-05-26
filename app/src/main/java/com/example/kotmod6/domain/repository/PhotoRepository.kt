package com.example.kotmod6.domain.repository

import com.example.kotmod6.domain.model.Photo

interface PhotoRepository {
    suspend fun loadPhotos(): List<Photo>
    suspend fun loadPhotoFile(photo: Photo): ByteArray
}
