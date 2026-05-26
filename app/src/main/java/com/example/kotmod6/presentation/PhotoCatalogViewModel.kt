package com.example.kotmod6.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotmod6.data.remote.NetworkModule
import com.example.kotmod6.data.repository.PicsumPhotoRepository
import com.example.kotmod6.domain.model.Photo
import com.example.kotmod6.domain.usecase.DownloadPhotoUseCase
import com.example.kotmod6.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PhotoCatalogViewModel(
    application: Application,
    private val getPhotosUseCase: GetPhotosUseCase,
    private val downloadPhotoUseCase: DownloadPhotoUseCase
) : AndroidViewModel(application) {

    private val _catalogState = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val catalogState: StateFlow<CatalogState> = _catalogState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _catalogState.value = CatalogState.Loading
            _catalogState.value = try {
                val photos = getPhotosUseCase()
                CatalogState.Success(photos)
            } catch (error: Exception) {
                CatalogState.Error(error.readableMessage())
            }
        }
    }

    fun findPhoto(id: String): Photo? {
        val currentState = _catalogState.value as? CatalogState.Success
        return currentState?.photos?.firstOrNull { it.id == id }
    }

    fun savePhotoBySaf(photo: Photo, targetUri: Uri) {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Saving
            _downloadState.value = try {
                val bytes = downloadPhotoUseCase(photo)

                // SAF уже дал Uri, поэтому просто пишем байты в выбранный пользователем файл.
                withContext(Dispatchers.IO) {
                    val resolver = getApplication<Application>().contentResolver
                    resolver.openOutputStream(targetUri)?.use { stream ->
                        stream.write(bytes)
                    } ?: throw IOException("Cannot open file")
                }

                DownloadState.Done("Photo saved")
            } catch (error: Exception) {
                DownloadState.Error(error.readableMessage())
            }
        }
    }

    fun clearDownloadMessage() {
        _downloadState.value = DownloadState.Idle
    }

    private fun Throwable.readableMessage(): String {
        return localizedMessage?.takeIf { it.isNotBlank() } ?: "Something went wrong"
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            val repository = PicsumPhotoRepository(
                api = NetworkModule.picsumApi,
                okHttpClient = NetworkModule.okHttpClient
            )
            val getPhotosUseCase = GetPhotosUseCase(repository)
            val downloadPhotoUseCase = DownloadPhotoUseCase(repository)

            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PhotoCatalogViewModel(
                        application = application,
                        getPhotosUseCase = getPhotosUseCase,
                        downloadPhotoUseCase = downloadPhotoUseCase
                    ) as T
                }
            }
        }
    }
}
