package com.example.kotmod6.presentation.detail

import androidx.compose.foundation.layout.Arrangement
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
import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.presentation.UserDetailState
import com.example.kotmod6.presentation.common.ErrorBlock
import com.example.kotmod6.presentation.common.LoadingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    state: UserDetailState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Профиль") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            UserDetailState.Idle,
            UserDetailState.Loading -> LoadingBlock(modifier = Modifier.padding(innerPadding))

            is UserDetailState.Error -> ErrorBlock(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            is UserDetailState.Success -> DetailContent(
                user = state.user,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DetailContent(
    user: AppUser,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.image)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar ${user.username}",
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = user.fullName,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        InfoLine(title = "Username", value = "@${user.username}")
        InfoLine(title = "Email", value = user.email)
    }
}

@Composable
private fun InfoLine(
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
