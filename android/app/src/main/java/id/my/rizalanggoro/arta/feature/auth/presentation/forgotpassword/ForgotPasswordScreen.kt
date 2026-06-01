package id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ForgotPasswordScreen(vm: ForgotPasswordVM = viewModel(factory = ForgotPasswordVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        email = uiState.email,
        isLoading = uiState.isLoading,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    email: String,
    isLoading: Boolean,
    onClickBack: () -> Unit,
) {
    androidx.compose.material3.Scaffold(
        topBar = { androidx.compose.material3.TopAppBar(title = { Text("Pemulihan Akun") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "PEMULIHAN",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Atur ulang akses akun dengan langkah yang jelas.",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Masukkan email terdaftar, lalu lanjutkan ke alur reset saat backend siap.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Email terdaftar",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Gunakan email yang sama dengan akun Arta Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "Email saat ini: ${if (email.isBlank()) "Belum diisi" else email}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Setelah backend reset password siap, tautan atau kode verifikasi bisa dikirim dari sini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                TextButton(
                    onClick = onClickBack,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    Text("Kembali ke login")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Forgot Password - Empty")
@Composable
private fun ForgotPasswordEmptyPreview() {
    ArtaTheme {
        Content(
            email = "",
            isLoading = false,
            onClickBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Forgot Password - Filled")
@Composable
private fun ForgotPasswordFilledPreview() {
    ArtaTheme {
        Content(
            email = "user@example.com",
            isLoading = false,
            onClickBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Forgot Password - Loading")
@Composable
private fun ForgotPasswordLoadingPreview() {
    ArtaTheme {
        Content(
            email = "user@example.com",
            isLoading = true,
            onClickBack = {},
        )
    }
}
