package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun LoginScreen(vm: LoginVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<LoginUiState.Event.ShowMessage>()
            .collect { event ->
                snackbarHostState.showSnackbar(event.message)
            }
    }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<LoginUiState.Event.LoginSucceeded>()
            .collect {
                backStack.add(Routes.HomeRoute)
                backStack.removeFirstOrNull()
            }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onChangeEmail = vm::onEmailChanged,
        onChangePassword = vm::onPasswordChanged,
        onClickSubmit = vm::onLoginClicked,
        onClickRegister = { backStack.add(RegisterRoute) },
        onClickForgotPassword = { backStack.add(ForgotPasswordRoute) },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: LoginUiState = LoginUiState(),
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Kelola uang dan emas dalam satu alur yang rapi",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Masuk untuk melihat ringkasan, transaksi, dan navigasi wallet yang sesuai tipe akun Anda.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        Text("Masuk")
                    }

                    TextButton(
                        onClick = onClickRegister,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Belum punya akun? Daftar sekarang")
                    }

                    TextButton(
                        onClick = onClickForgotPassword,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Lupa kata sandi")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = LoginUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginErrorPreview() {
    ArtaTheme {
        Content(
            uiState = LoginUiState(
                emailError = "Email wajib diisi",
                passwordError = "Kata sandi wajib diisi",
            ),
        )
    }
}