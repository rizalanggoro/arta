package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(vm: RegisterVM = registerViewModel()) {
	val uiState by vm.uiState.collectAsState()

	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		vm.messageEvent.collect { message ->
			snackbarHostState.showSnackbar(message)
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
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
	snackbarHostState: SnackbarHostState,
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
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Register") }
			)
		},
		snackbarHost = {
			SnackbarHost(hostState = snackbarHostState)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Buat akun baru",
				style = MaterialTheme.typography.headlineSmall
			)

			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
				)
			}

			when (isLoading) {
				true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
				false -> Button(
					onClick = onClickSubmit,
					modifier = Modifier.fillMaxWidth(),
					enabled = !isLoading,
				) {
					Text("Daftar")
				}
			}

			TextButton(
				onClick = onClickSubmit,
				modifier = Modifier.align(Alignment.CenterHorizontally),
				enabled = !isLoading,
			) {
				Text("Sudah punya akun? Masuk sekarang")
			}
		}
	}
}

@Composable
private fun registerViewModel(): RegisterVM {
	return viewModel(factory = RegisterVM.Factory)
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
		)
	}
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterLoadingPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			isLoading = true,
		)
	}
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterErrorPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			nameError = "Nama wajib diisi",
			emailError = "Email wajib diisi",
			passwordError = "Kata sandi wajib diisi",
			confirmPasswordError = "Konfirmasi kata sandi wajib diisi",
		)
	}
}