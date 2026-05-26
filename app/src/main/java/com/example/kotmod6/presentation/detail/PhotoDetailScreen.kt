package com.example.kotmod6.presentation.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kotmod6.domain.model.Photo
import com.example.kotmod6.presentation.CatalogState
import com.example.kotmod6.presentation.DownloadState
import com.example.kotmod6.presentation.common.ErrorBlock
import com.example.kotmod6.presentation.common.LoadingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: Photo?,
    loadingState: CatalogState,
    downloadState: DownloadState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onDownload: (Photo, Uri) -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(downloadState) {
        when (downloadState) {
            is DownloadState.Done -> {
                snackbarHostState.showSnackbar(downloadState.message)
                onMessageShown()
            }

            is DownloadState.Error -> {
                snackbarHostState.showSnackbar(downloadState.message)
                onMessageShown()
            }

            DownloadState.Idle,
            DownloadState.Saving -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = photo?.author ?: "Детали фото",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when {
            photo != null -> DetailContent(
                photo = photo,
                downloadState = downloadState,
                onDownload = onDownload,
                modifier = Modifier.padding(innerPadding)
            )

            loadingState is CatalogState.Loading -> LoadingBlock(modifier = Modifier.padding(innerPadding))
            loadingState is CatalogState.Error -> ErrorBlock(
                message = loadingState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            else -> ErrorBlock(
                message = "Фото не найдено",
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DetailContent(
    photo: Photo,
    downloadState: DownloadState,
    onDownload: (Photo, Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg"),
        onResult = { uri ->
            if (uri != null) {
                onDownload(photo, uri)
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.largeImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Большое фото автора ${photo.author}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoLine(title = "Автор", value = photo.author)
            InfoLine(title = "Размер", value = photo.sizeLabel)
            InfoLine(title = "Ссылка", value = photo.sourceUrl)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                enabled = downloadState !is DownloadState.Saving,
                onClick = { launcher.launch("picsum_${photo.id}.jpg") }
            ) {
                Text(text = "Скачать фото")
            }

            if (downloadState is DownloadState.Saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun InfoLine(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
