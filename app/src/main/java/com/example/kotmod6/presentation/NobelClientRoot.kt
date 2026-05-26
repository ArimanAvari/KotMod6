package com.example.kotmod6.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.kotmod6.domain.model.NobelPrize

@Composable
fun NobelClientRoot(
    state: NobelClientState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRetry: () -> Unit,
    onFilterYearChange: (String) -> Unit,
    onFilterCategoryChange: (String) -> Unit,
    onApplyFilter: () -> Unit,
    onOpenPrize: (NobelPrize) -> Unit,
    onBack: () -> Unit,
    onAddFavorite: (NobelPrize) -> Unit,
    onRemoveFavorite: (NobelPrize) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenList: () -> Unit,
    onLogout: () -> Unit
) {
    if (state.checkingToken) {
        LoadingScreen()
        return
    }

    when (state.screen) {
        AppScreen.Login -> LoginScreen(state, onUsernameChange, onPasswordChange, onLogin)
        AppScreen.List -> PrizeListScreen(
            state = state,
            onRetry = onRetry,
            onOpenPrize = onOpenPrize,
            onOpenFavorites = onOpenFavorites,
            onFilterYearChange = onFilterYearChange,
            onFilterCategoryChange = onFilterCategoryChange,
            onApplyFilter = onApplyFilter,
            onLogout = onLogout
        )

        AppScreen.Detail -> PrizeDetailScreen(
            state = state,
            prize = state.selectedPrize,
            onBack = onBack,
            onAddFavorite = onAddFavorite,
            onRemoveFavorite = onRemoveFavorite,
            onLogout = onLogout
        )

        AppScreen.Favorites -> FavoritesScreen(
            state = state,
            onOpenPrize = onOpenPrize,
            onOpenList = onOpenList,
            onRemoveFavorite = onRemoveFavorite,
            onLogout = onLogout
        )
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoginScreen(
    state: NobelClientState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nobel API", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Вход на сервер задания 5", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            StatusText(state)
            Button(
                onClick = onLogin,
                enabled = !state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (state.loading) CircularProgressIndicator(Modifier.padding(end = 10.dp), strokeWidth = 2.dp)
                Text("Войти")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrizeListScreen(
    state: NobelClientState,
    onRetry: () -> Unit,
    onOpenPrize: (NobelPrize) -> Unit,
    onOpenFavorites: () -> Unit,
    onFilterYearChange: (String) -> Unit,
    onFilterCategoryChange: (String) -> Unit,
    onApplyFilter: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Премии") },
                actions = {
                    TextButton(onClick = onOpenFavorites) { Text("Избранное") }
                    TextButton(onClick = onLogout) { Text("Выйти") }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            FilterBar(state, onFilterYearChange, onFilterCategoryChange, onApplyFilter)
            StatusText(state)
            if (state.loading) {
                LoadingScreen()
            } else if (state.error != null) {
                ErrorBlock(state.error, onRetry)
            } else {
                PrizeList(state.visiblePrizes, onOpenPrize)
            }
        }
    }
}

@Composable
private fun FilterBar(
    state: NobelClientState,
    onYear: (String) -> Unit,
    onCategory: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = state.filterYear,
                onValueChange = onYear,
                modifier = Modifier.weight(0.7f),
                label = { Text("Год") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.filterCategory,
                onValueChange = onCategory,
                modifier = Modifier.weight(1f),
                label = { Text("Категория") },
                singleLine = true
            )
        }
        Button(onClick = onApply, modifier = Modifier.fillMaxWidth()) {
            Text("Применить")
        }
    }
}

@Composable
private fun PrizeList(
    prizes: List<NobelPrize>,
    onOpenPrize: (NobelPrize) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(prizes, key = { it.id }) { prize ->
            PrizeCard(prize, onClick = { onOpenPrize(prize) })
        }
    }
}

@Composable
private fun PrizeCard(prize: NobelPrize, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(prize.title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(prize.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${prize.laureates.size} лауреатов", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrizeDetailScreen(
    state: NobelClientState,
    prize: NobelPrize?,
    onBack: () -> Unit,
    onAddFavorite: (NobelPrize) -> Unit,
    onRemoveFavorite: (NobelPrize) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } },
                actions = { TextButton(onClick = onLogout) { Text("Выйти") } }
            )
        }
    ) { innerPadding ->
        if (prize == null) {
            ErrorBlock("Премия не выбрана", onBack)
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(prize.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(prize.description)
            Text("Raw JSON: ${prize.rawJson}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            prize.laureates.forEach { laureate ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(laureate.fullName, fontWeight = FontWeight.SemiBold)
                        Text(laureate.birthCountry, color = MaterialTheme.colorScheme.primary)
                        Text(laureate.motivation)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { onAddFavorite(prize) }, enabled = !state.loading) {
                    Text("В избранное")
                }
                OutlinedButton(onClick = { onRemoveFavorite(prize) }, enabled = !state.loading) {
                    Text("Удалить")
                }
            }
            StatusText(state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(
    state: NobelClientState,
    onOpenPrize: (NobelPrize) -> Unit,
    onOpenList: () -> Unit,
    onRemoveFavorite: (NobelPrize) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = { TextButton(onClick = onOpenList) { Text("Назад") } },
                actions = { TextButton(onClick = onLogout) { Text("Выйти") } }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            StatusText(state)
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.favorites, key = { it.id }) { prize ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(prize.title, fontWeight = FontWeight.Bold)
                            Text(prize.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { onOpenPrize(prize) }) { Text("Открыть") }
                                OutlinedButton(onClick = { onRemoveFavorite(prize) }) { Text("Удалить") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusText(state: NobelClientState) {
    state.error?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
    state.message?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun ErrorBlock(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 14.dp)) {
            Text("Повторить")
        }
    }
}
