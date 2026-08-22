package id.my.rizalanggoro.arta.feature.auth.presentation.login

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
import id.my.rizalanggoro.arta.core.application.route.AuthRoute
import id.my.rizalanggoro.arta.core.application.route.HomeRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ErrorColor = Color(0xFFE53935)

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
                backStack.add(HomeRoute.Index)
                backStack.removeFirstOrNull()
            }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onChangeEmail = vm::onEmailChanged,
        onChangePassword = vm::onPasswordChanged,
        onClickSubmit = vm::onLoginClicked,
        onClickRegister = { backStack.add(AuthRoute.Register) },
        onClickForgotPassword = { backStack.add(AuthRoute.ForgotPassword) },
    )
}

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
            SmallTopAppBar(
                title = "Masuk"
            )
        },
        snackbarHost = {
            SnackbarHost(state = snackbarHostState)
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Masuk untuk melihat ringkasan, transaksi, dan navigasi wallet yang sesuai tipe akun Anda.",
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imeAction = ImeAction.Done,
                        ),
                    )
                    uiState.passwordError?.let { error ->
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
                        Text("Masuk")
                    }

                    TextButton(
                        text = "Belum punya akun? Daftar sekarang",
                        onClick = onClickRegister,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextButton(
                        text = "Lupa kata sandi",
                        onClick = onClickForgotPassword,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginPreview() {
    ArtaMiuixTheme {
        Content()
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginLoadingPreview() {
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content(
            uiState = LoginUiState(
                emailError = "Email wajib diisi",
                passwordError = "Kata sandi wajib diisi",
            ),
        )
    }
}
