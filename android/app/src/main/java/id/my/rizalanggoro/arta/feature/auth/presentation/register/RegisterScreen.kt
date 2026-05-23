package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.WalletCreateFirstRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegisterScreen(vm: RegisterVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<RegisterUiState.Event.ShowMessage>()
            .collect { event ->
                snackbarHostState.showSnackbar(event.message)
            }
    }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<RegisterUiState.Event.RegisterSucceeded>()
            .collect {
                backStack.clear()
                backStack.add(WalletCreateFirstRoute)
            }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onChangeName = vm::onNameChanged,
        onChangeEmail = vm::onEmailChanged,
        onChangePassword = vm::onPasswordChanged,
        onChangeConfirmPassword = vm::onConfirmPasswordChanged,
        onClickSubmit = vm::onRegisterClicked,
        onClickLogin = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: RegisterUiState = RegisterUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onChangeConfirmPassword: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickLogin: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registrasi") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Buat akun baru yang siap dipakai untuk uang dan emas.",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Registrasi dibentuk untuk cepat, jelas, dan tetap nyaman " +
                            "dipakai di layar mobile",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = uiState.name,
                    onValueChange = onChangeName,
                    label = { Text("Nama lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.nameError != null,
                    supportingText = when {
                        uiState.nameError != null -> {
                            { Text(uiState.nameError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                TextField(
                    value = uiState.email,
                    onValueChange = onChangeEmail,
                    label = { Text("Alamat email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.emailError != null,
                    supportingText = when {
                        uiState.emailError != null -> {
                            { Text(uiState.emailError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )

                TextField(
                    value = uiState.password,
                    onValueChange = onChangePassword,
                    label = { Text("Kata sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.passwordError != null,
                    supportingText = when {
                        uiState.passwordError != null -> {
                            { Text(uiState.passwordError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                )

                TextField(
                    value = uiState.confirmPassword,
                    onValueChange = onChangeConfirmPassword,
                    label = { Text("Konfirmasi kata sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.confirmPasswordError != null,
                    supportingText = when {
                        uiState.confirmPasswordError != null -> {
                            { Text(uiState.confirmPasswordError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            when (uiState.isLoading) {
                true -> LoadingIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                else -> Column {
                    Button(
                        onClick = onClickSubmit,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Daftar")
                    }

                    TextButton(
                        onClick = onClickLogin,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Sudah punya akun? Masuk sekarang")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = RegisterUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterErrorPreview() {
    ArtaTheme {
        Content(
            uiState = RegisterUiState(
                nameError = "Nama wajib diisi",
                emailError = "Email wajib diisi",
                passwordError = "Kata sandi wajib diisi",
                confirmPasswordError = "Konfirmasi kata sandi wajib diisi",
            ),
        )
    }
}