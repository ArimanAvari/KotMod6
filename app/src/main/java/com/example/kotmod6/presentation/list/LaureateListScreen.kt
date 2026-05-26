package com.example.kotmod6.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.kotmod6.domain.model.LaureatePrize
import com.example.kotmod6.domain.model.PrizeCategory
import com.example.kotmod6.presentation.FilterState
import com.example.kotmod6.presentation.LaureateListState
import com.example.kotmod6.presentation.common.ErrorBlock
import com.example.kotmod6.presentation.common.LoadingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureateListScreen(
    state: LaureateListState,
    filterState: FilterState,
    onYearChange: (String) -> Unit,
    onCategoryChange: (PrizeCategory) -> Unit,
    onApplyFilters: () -> Unit,
    onRetry: () -> Unit,
    onLaureateClick: (LaureatePrize) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Нобелевские лауреаты") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            FilterPanel(
                filterState = filterState,
                onYearChange = onYearChange,
                onCategoryChange = onCategoryChange,
                onApplyFilters = onApplyFilters
            )

            when (state) {
                LaureateListState.Loading -> LoadingBlock()
                is LaureateListState.Error -> ErrorBlock(
                    message = state.message,
                    onRetry = onRetry
                )

                is LaureateListState.Success -> LaureateList(
                    laureates = state.laureates,
                    onLaureateClick = onLaureateClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPanel(
    filterState: FilterState,
    onYearChange: (String) -> Unit,
    onCategoryChange: (PrizeCategory) -> Unit,
    onApplyFilters: () -> Unit
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
                value = filterState.year,
                onValueChange = onYearChange,
                modifier = Modifier.weight(0.8f),
                singleLine = true,
                label = { Text(text = "Год") },
                placeholder = { Text(text = "2023") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1.2f)
            ) {
                OutlinedTextField(
                    value = filterState.selectedCategory.title,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text(text = "Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    PrizeCategory.Values.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.title) },
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
            onClick = onApplyFilters,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Показать")
        }
    }
}

@Composable
private fun LaureateList(
    laureates: List<LaureatePrize>,
    onLaureateClick: (LaureatePrize) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = laureates,
            key = { "${it.laureateId}_${it.awardYear}_${it.categoryCode}" }
        ) { laureate ->
            LaureateCard(
                laureate = laureate,
                onClick = { onLaureateClick(laureate) }
            )
        }
    }
}

@Composable
private fun LaureateCard(
    laureate: LaureatePrize,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${laureate.awardYear} · ${laureate.category}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = laureate.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = laureate.shortMotivation.ifBlank { "Описание не указано" },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
