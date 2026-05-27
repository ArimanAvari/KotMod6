package com.example.kotmod6.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NobelClientRoot(
    state: NobelClientState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRetry: () -> Unit,
    onFilterYearChange: (String) -> Unit,
    onFilterCategoryChange: (PrizeCategory) -> Unit,
    onApplyFilter: () -> Unit,
    onOpenLaureate: (LaureateEntry) -> Unit,
    onBack: () -> Unit
) {
    if (state.checkingToken) {
        LoadingBlock()
        return
    }

    when (state.screen) {
        AppScreen.Login -> LoginScreen(state, onUsernameChange, onPasswordChange, onLogin)
        AppScreen.List -> LaureateListScreen(
            state = state,
            onRetry = onRetry,
            onYearChange = onFilterYearChange,
            onCategoryChange = onFilterCategoryChange,
            onApplyFilter = onApplyFilter,
            onLaureateClick = onOpenLaureate
        )

        AppScreen.Detail -> LaureateDetailScreen(
            state = state,
            onBack = onBack
        )
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
private fun LaureateListScreen(
    state: NobelClientState,
    onRetry: () -> Unit,
    onYearChange: (String) -> Unit,
    onCategoryChange: (PrizeCategory) -> Unit,
    onApplyFilter: () -> Unit,
    onLaureateClick: (LaureateEntry) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Нобелевские лауреаты") }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            FilterPanel(
                state = state,
                onYearChange = onYearChange,
                onCategoryChange = onCategoryChange,
                onApplyFilter = onApplyFilter
            )

            if (state.loading) {
                LoadingBlock()
            } else if (state.error != null) {
                ErrorBlock(state.error, onRetry)
            } else {
                LaureateList(
                    laureates = state.visibleLaureates,
                    onLaureateClick = onLaureateClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPanel(
    state: NobelClientState,
    onYearChange: (String) -> Unit,
    onCategoryChange: (PrizeCategory) -> Unit,
    onApplyFilter: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = state.filterYear,
                onValueChange = onYearChange,
                modifier = Modifier.weight(0.8f),
                singleLine = true,
                label = { Text("Год") },
                placeholder = { Text("2023") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1.2f)
            ) {
                OutlinedTextField(
                    value = state.selectedCategory.title,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    PrizeCategory.Values.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.title) },
                            onClick = {
                                onCategoryChange(category)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onApplyFilter,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Применить фильтр")
        }
    }
}

@Composable
private fun LaureateList(
    laureates: List<LaureateEntry>,
    onLaureateClick: (LaureateEntry) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(laureates, key = { it.key }) { item ->
            LaureateCard(
                item = item,
                onClick = { onLaureateClick(item) }
            )
        }
    }
}

@Composable
private fun LaureateCard(
    item: LaureateEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.year,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.category,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = item.laureate.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.shortMotivation.ifBlank { "Описание не указано" },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaureateDetailScreen(
    state: NobelClientState,
    onBack: () -> Unit
) {
    val item = state.selectedLaureate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Подробности") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.loading) {
            LoadingBlock(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        if (state.error != null) {
            ErrorBlock(state.error, onBack, modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        if (item == null) {
            ErrorBlock("Лауреат не выбран", onBack, modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            InitialsCircle(item.laureate.fullName)
            Text(
                text = item.laureate.fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            InfoBlock(title = "Премия", value = "${item.year} · ${item.category}")
            InfoBlock(title = "Мотивация", value = item.laureate.motivation.ifBlank { "Описание не указано" })
            InfoBlock(title = "Место рождения", value = item.laureate.birthCountry)
        }
    }
}

@Composable
private fun InitialsCircle(name: String) {
    Box(
        modifier = Modifier
            .size(132.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.initials(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
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

@Composable
private fun LoadingBlock(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorBlock(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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

@Composable
private fun StatusText(state: NobelClientState) {
    state.error?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 10.dp)
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
