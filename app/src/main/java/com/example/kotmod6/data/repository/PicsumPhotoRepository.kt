package com.example.kotmod6.data.repository

import com.example.kotmod6.data.mapper.toDomain
import com.example.kotmod6.data.remote.PicsumApi
import com.example.kotmod6.domain.model.Photo
import com.example.kotmod6.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PicsumPhotoRepository(
    private val api: PicsumApi,
    private val okHttpClient: OkHttpClient
) : PhotoRepository {
    override suspend fun loadPhotos(): List<Photo> = api.getPhotos().map { it.toDomain() }

    override suspend fun loadPhotoFile(photo: Photo): ByteArray = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(photo.downloadUrl)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Server returned ${response.code}")
            }

            response.body?.bytes() ?: throw IOException("Empty photo file")
        }
    }
}
