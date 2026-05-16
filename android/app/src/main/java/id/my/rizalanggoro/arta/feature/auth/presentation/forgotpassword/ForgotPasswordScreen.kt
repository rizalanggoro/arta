package id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(vm: ForgotPasswordVM = viewModel()) {
	val uiState by vm.uiState.collectAsState()

	Content(
		email = uiState.email,
		isLoading = uiState.isLoading,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
	email: String,
	isLoading: Boolean,
) {
	val backStack = LocalBackStack.current

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		TopAppBar(title = { Text("Lupa Kata Sandi") })
		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Masukkan email untuk reset kata sandi.", style = MaterialTheme.typography.bodyLarge)
		Spacer(modifier = Modifier.height(12.dp))
		Text(text = "Email: ${if (email.isBlank()) "-" else email}")
		Spacer(modifier = Modifier.height(24.dp))
		Button(onClick = { backStack.removeLastOrNull() }, enabled = !isLoading) {
			Text("Kembali")
		}
	}
}
