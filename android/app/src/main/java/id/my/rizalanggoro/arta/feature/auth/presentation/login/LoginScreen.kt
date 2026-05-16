package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun LoginScreen(vm: LoginVM = viewModel(factory = LoginVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.messageEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        isLoading = uiState.isLoading,
        email = uiState.email,
        password = uiState.password,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        onChangeEmail = vm::onChangeEmail,
        onChangePassword = vm::onChangePassword,
        onClickSubmit = vm::login,
        onClickRegister = { backStack.add(RegisterRoute) },
        onClickForgotPassword = { backStack.add(ForgotPasswordRoute) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean = false,
    email: String = "",
    password: String = "",
    emailError: String? = null,
    passwordError: String? = null,
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickRegister: () -> Unit = {},
    onClickForgotPassword: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
				title = {
                Text("Masuk")
            }
			)
        }
    ) {
        Column(
            modifier = Modifier
				.fillMaxSize()
				.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "MASUK",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Kelola uang dan emas dalam satu alur yang rapi.",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Masuk untuk melihat ringkasan, transaksi, dan navigasi wallet yang sesuai tipe akun Anda.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SnackbarHost(hostState = snackbarHostState)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Masukkan kredensial Anda",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Gunakan email aktif dan kata sandi yang terdaftar di Arta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = onChangeEmail,
                        label = { Text("Alamat email") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) Text(emailError)
                        },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = onChangePassword,
                        label = { Text("Kata sandi") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) Text(passwordError)
                        },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    )
                }

                Button(
                    onClick = onClickSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
								.size(20.dp)
								.align(Alignment.CenterVertically),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Masuk")
                    }
                }

                TextButton(
                    onClick = onClickForgotPassword,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    Text("Lupa kata sandi")
                }

                TextButton(
                    onClick = onClickRegister,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isLoading,
                ) {
                    Text("Belum punya akun? Daftar")
                }
            }
        }
    }
}

// ViewModel is initialized directly on the composable parameter per project convention

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginLoadingPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true,
        )
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginErrorPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            emailError = "Email wajib diisi",
            passwordError = "Kata sandi wajib diisi",
        )
    }
}