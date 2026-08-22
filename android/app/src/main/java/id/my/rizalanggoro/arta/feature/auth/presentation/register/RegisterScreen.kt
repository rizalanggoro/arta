package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ErrorColor = Color(0xFFE53935)

@Composable
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
                backStack.add(WalletRoute.CreateFirst)
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
            SmallTopAppBar(title = "Registrasi")
        },
        snackbarHost = { SnackbarHost(state = snackbarHostState) },
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Registrasi dibentuk untuk cepat, jelas, dan tetap nyaman " +
                            "dipakai di layar mobile",
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextField(
                        value = uiState.name,
                        onValueChange = onChangeName,
                        label = "Nama lengkap",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        colors = if (uiState.nameError != null) {
                            TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                        } else {
                            TextFieldDefaults.textFieldColors()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    uiState.nameError?.let { error ->
                        Text(error, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextField(
                        value = uiState.email,
                        onValueChange = onChangeEmail,
                        label = "Alamat email",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        colors = if (uiState.emailError != null) {
                            TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                        } else {
                            TextFieldDefaults.textFieldColors()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    uiState.emailError?.let { error ->
                        Text(error, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextField(
                        value = uiState.password,
                        onValueChange = onChangePassword,
                        label = "Kata sandi",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        colors = if (uiState.passwordError != null) {
                            TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                        } else {
                            TextFieldDefaults.textFieldColors()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    uiState.passwordError?.let { error ->
                        Text(error, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextField(
                        value = uiState.confirmPassword,
                        onValueChange = onChangeConfirmPassword,
                        label = "Konfirmasi kata sandi",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        colors = if (uiState.confirmPasswordError != null) {
                            TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                        } else {
                            TextFieldDefaults.textFieldColors()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    )
                    uiState.confirmPasswordError?.let { error ->
                        Text(error, fontSize = 13.sp, color = ErrorColor)
                    }
                }
            }

            when (uiState.isLoading) {
                true -> InfiniteProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MiuixTheme.colorScheme.primary
                )

                else -> Column {
                    Button(
                        onClick = onClickSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColorsPrimary()
                    ) {
                        Text("Daftar")
                    }

                    TextButton(
                        text = "Sudah punya akun? Masuk sekarang",
                        onClick = onClickLogin,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterPreview() {
    ArtaMiuixTheme {
        Content()
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterLoadingPreview() {
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
