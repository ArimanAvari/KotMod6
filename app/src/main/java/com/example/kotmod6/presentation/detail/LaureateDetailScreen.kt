package com.example.kotmod6.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.presentation.LaureateDetailState
import com.example.kotmod6.presentation.common.ErrorBlock
import com.example.kotmod6.presentation.common.LoadingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureateDetailScreen(
    laureateId: String,
    year: String,
    categoryCode: String,
    state: LaureateDetailState,
    onLoad: (String, String, String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(laureateId, year, categoryCode) {
        onLoad(laureateId, year, categoryCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Подробности") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            LaureateDetailState.Idle,
            LaureateDetailState.Loading -> LoadingBlock(modifier = Modifier.padding(innerPadding))

            is LaureateDetailState.Error -> ErrorBlock(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            is LaureateDetailState.Success -> DetailContent(
                detail = state.detail,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DetailContent(
    detail: LaureateDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Portrait(detail = detail)

        Text(
            text = detail.fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        InfoBlock(title = "Премия", value = "${detail.awardYear} · ${detail.category}")
        InfoBlock(title = "Мотивация", value = detail.motivation.ifBlank { "Описание не указано" })
        InfoBlock(title = "Место рождения", value = detail.birthPlace)

        detail.wikipediaUrl?.let { link ->
            InfoBlock(title = "Wikipedia", value = link)
        }
        detail.nobelPageUrl?.let { link ->
            InfoBlock(title = "Nobel Prize", value = link)
        }
    }
}

@Composable
private fun Portrait(detail: LaureateDetail) {
    if (detail.portraitUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(detail.portraitUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Фото ${detail.fullName}",
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = detail.fullName.initials(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoBlock(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun String.initials(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
}
