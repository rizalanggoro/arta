package id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
private fun Content(
    email: String,
    isLoading: Boolean,
    onClickBack: () -> Unit,
) {
    Scaffold(
        topBar = { SmallTopAppBar(title = "Pemulihan Akun") }
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
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Text(
                text = "Atur ulang akses akun dengan langkah yang jelas.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Masukkan email terdaftar, lalu lanjutkan ke alur reset saat backend siap.",
                fontSize = 16.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Email terdaftar",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Gunakan email yang sama dengan akun Arta Anda.",
                    fontSize = 14.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )

                Text(
                    text = "Email saat ini: ${if (email.isBlank()) "Belum diisi" else email}",
                    fontSize = 16.sp,
                )
                Text(
                    text = "Setelah backend reset password siap, tautan atau kode verifikasi bisa dikirim dari sini.",
                    fontSize = 14.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )

                TextButton(
                    text = "Kembali ke login",
                    onClick = onClickBack,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Forgot Password - Empty")
@Composable
private fun ForgotPasswordEmptyPreview() {
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content(
            email = "user@example.com",
            isLoading = true,
            onClickBack = {},
        )
    }
}
