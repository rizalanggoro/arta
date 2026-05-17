package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

// ... (rest of imports)
// Keep the rest of the file structure as is for now, I will insert Scaffold around Content

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegisterScreen(vm: RegisterVM = viewModel(factory = RegisterVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.messageEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterEffect.NavigateToCreateWallet -> {
                    backStack.add(id.my.rizalanggoro.arta.core.Routes.WalletCreateRoute)
                }
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        isLoading = uiState.isLoading,
        name = uiState.name,
        email = uiState.email,
        password = uiState.password,
        confirmPassword = uiState.confirmPassword,
        nameError = uiState.nameError,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        confirmPasswordError = uiState.confirmPasswordError,
        onChangeName = vm::onChangeName,
        onChangeEmail = vm::onChangeEmail,
        onChangePassword = vm::onChangePassword,
        onChangeConfirmPassword = vm::onChangeConfirmPassword,
        onClickSubmit = vm::register,
        onClickLogin = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isLoading: Boolean = false,
    name: String = "",
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    nameError: String? = null,
    emailError: String? = null,
    passwordError: String? = null,
    confirmPasswordError: String? = null,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Buat akun baru yang siap dipakai untuk uang dan emas.",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Registrasi dibentuk untuk cepat, jelas, dan tetap nyaman dipakai di layar mobile.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Column() {
                Column() {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onChangeName,
                        label = { Text("Nama lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) Text(nameError)
                        },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    )

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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onChangeConfirmPassword,
                        label = { Text("Konfirmasi kata sandi") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmPasswordError != null,
                        supportingText = {
                            if (confirmPasswordError != null) Text(confirmPasswordError)
                        },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Daftar")
                    }
                }

                TextButton(
                    onClick = onClickLogin,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    Text("Sudah punya akun? Masuk sekarang")
                }
            }
        }
    }
}

// ViewModel is initialized directly on the composable parameter per project convention

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
        Content(isLoading = true)
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterErrorPreview() {
    ArtaTheme {
        Content(
            nameError = "Nama wajib diisi",
            emailError = "Email wajib diisi",
            passwordError = "Kata sandi wajib diisi",
            confirmPasswordError = "Konfirmasi kata sandi wajib diisi",
        )
    }
}