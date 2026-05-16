package id.my.rizalanggoro.arta.feature.category.presentation.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun UpdateCategoryScreen(
	categoryId: Int,
	vm: UpdateCategoryVM = viewModel(factory = UpdateCategoryVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	val backStack = LocalBackStack.current

	LaunchedEffect(categoryId) {
		vm.loadCategory(categoryId)
	}

	LaunchedEffect(Unit) {
		vm.effect.collect { effect ->
			when (effect) {
				is UpdateCategoryEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
				UpdateCategoryEffect.NavigateBack -> backStack.removeLastOrNull()
			}
		}
	}

	Content(
		snackbarHostState = snackbarHostState,
		name = uiState.name,
		type = uiState.type,
		icon = uiState.icon,
		color = uiState.color,
		nameError = uiState.nameError,
		errorMessage = uiState.errorMessage,
		isLoading = uiState.isLoading,
		onChangeName = vm::onChangeName,
		onChangeType = vm::onChangeType,
		onChangeIcon = vm::onChangeIcon,
		onChangeColor = vm::onChangeColor,
		onClickSubmit = vm::updateCategory,
		onClickBack = { backStack.removeLastOrNull() },
	)
}

@Composable
private fun Content(
	snackbarHostState: SnackbarHostState,
	name: String = "",
	type: String = "expense",
	icon: String = "🧾",
	color: String = "#E11D48",
	nameError: String? = null,
	errorMessage: String? = null,
	isLoading: Boolean = false,
	onChangeName: (String) -> Unit = {},
	onChangeType: (String) -> Unit = {},
	onChangeIcon: (String) -> Unit = {},
	onChangeColor: (String) -> Unit = {},
	onClickSubmit: () -> Unit = {},
	onClickBack: () -> Unit = {},
) {
	val typeOptions = listOf(
		CategoryTypeOption(label = "Pengeluaran", value = "expense"),
		CategoryTypeOption(label = "Pemasukan", value = "income"),
		CategoryTypeOption(label = "Umum", value = "general"),
	)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Text(
			text = "KATEGORI",
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Text(
			text = "Ubah kategori transaksi.",
			style = MaterialTheme.typography.headlineMedium,
		)
		Text(
			text = "Sesuaikan nama, tipe, ikon, dan warna agar kategori tetap mudah dikenali.",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Text(
			text = "Edit kategori  ·  Ikon  ·  Warna",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		SnackbarHost(hostState = snackbarHostState)

		Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
			Text(
				text = "Perbarui informasi kategori custom.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			if (errorMessage != null) {
				Text(
					text = errorMessage,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)
			}

			OutlinedTextField(
				value = name,
				onValueChange = onChangeName,
				label = { Text("Nama kategori") },
				modifier = Modifier.fillMaxWidth(),
				isError = nameError != null,
				supportingText = {
					if (nameError != null) Text(nameError)
				},
				enabled = !isLoading,
				singleLine = true,
			)

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					text = "Tipe kategori",
					style = MaterialTheme.typography.labelLarge,
				)
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					typeOptions.forEach { option ->
						FilterChip(
							selected = type == option.value,
							onClick = { onChangeType(option.value) },
							label = { Text(option.label) },
							enabled = !isLoading,
						)
					}
				}
			}

			OutlinedTextField(
				value = icon,
				onValueChange = onChangeIcon,
				label = { Text("Ikon") },
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
				singleLine = true,
			)

			OutlinedTextField(
				value = color,
				onValueChange = onChangeColor,
				label = { Text("Warna") },
				modifier = Modifier.fillMaxWidth(),
				supportingText = { Text("Contoh: #E11D48") },
				enabled = !isLoading,
				singleLine = true,
			)

			Button(
				onClick = onClickSubmit,
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
			) {
				if (isLoading) {
					CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
				} else {
					Text("Simpan Perubahan")
				}
			}

			FilledTonalButton(
				onClick = onClickBack,
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
			) {
				Text("Batal")
			}
		}
	}
}

private data class CategoryTypeOption(
	val label: String,
	val value: String,
)

@Preview(showBackground = true, name = "Update Category - Default")
@Composable
private fun UpdateCategoryDefaultPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() })
	}
}

@Preview(showBackground = true, name = "Update Category - Loading")
@Composable
private fun UpdateCategoryLoadingPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			isLoading = true,
		)
	}
}