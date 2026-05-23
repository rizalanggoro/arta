package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun SelectCategoryScreen(
    vm: SelectCategoryVM = viewModel(factory = SelectCategoryVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategorySelected>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        categories = uiState.categories,
        selectedType = uiState.selectedType,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onReload = vm::loadCategories,
        onClickType = vm::onCategoryTypeSelected,
        onSelect = vm::selectCategory,
        onBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun SelectCategoryCard(
    category: DtoCategory,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (category.data.type) {
                            "income" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
            ) {
                Icon(
                    when (category.data.type) {
                        "income" -> Icons.AutoMirrored.Rounded.CallReceived
                        else -> Icons.AutoMirrored.Rounded.CallMade
                    },
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = when (category.data.type) {
                        "income" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(category.data.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = category.data.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Content(
    categories: List<DtoCategory> = emptyList(),
    selectedType: String = "expense",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onReload: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onSelect: (DomainCategory) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val visibleCategories = categories.filter { it.data.type == selectedType }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Pilih kategori yang akan dipakai di transaksi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            categoryTypes.mapIndexed { index, item ->
                SegmentedButton(
                    selected = selectedType == item.value,
                    onClick = { onClickType(item.value) },
                    shape = SegmentedButtonDefaults.itemShape(
                        count = categoryTypes.size,
                        index = index,
                    ),
                ) {
                    Text(item.name)
                }
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(errorMessage)
                        Button(onClick = onReload) { Text("Muat ulang") }
                    }
                }
            }

            visibleCategories.isEmpty() -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Belum ada kategori ${if (selectedType == "income") "pemasukan" else "pengeluaran"}.")
                        Text(
                            text = "Buat kategori dulu agar bisa dipakai di transaksi.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visibleCategories, key = { it.data.id }) { category ->
                        SelectCategoryCard(
                            category = category,
                            onClick = { onSelect(category.data) },
                        )
                    }
                }
            }
        }

        TextButton(onClick = onBack) {
            Text("Batal")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Select Category - With Items")
@Composable
private fun SelectCategoryItemsPreview() {
    ArtaTheme {
        Content(
            categories = listOf(
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 1,
                        name = "Makanan",
                        type = "expense",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = null,
                    )
                ),
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 2,
                        name = "Gaji",
                        type = "income",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = 10,
                    )
                ),
            ),
            selectedType = "expense",
        )
    }
}

@Preview(showBackground = true, name = "Select Category - Loading")
@Composable
private fun SelectCategoryLoadingPreview() {
    ArtaTheme { Content() }
}
