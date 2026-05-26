package com.example.kotmod6.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.kotmod6.presentation.common.ErrorBlock
import com.example.kotmod6.presentation.common.LoadingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    state: CatalogState,
    onRetry: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Фотокаталог")
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            CatalogState.Loading -> LoadingBlock(modifier = Modifier.padding(innerPadding))
            is CatalogState.Error -> ErrorBlock(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            is CatalogState.Success -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.photos,
                    key = { it.id }
                ) { photo ->
                    PhotoCard(
                        photo = photo,
                        onClick = { onPhotoClick(photo) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.smallImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Фото автора ${photo.author}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.18f)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = photo.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = photo.sizeLabel,
                    modifier = Modifier.padding(top = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
