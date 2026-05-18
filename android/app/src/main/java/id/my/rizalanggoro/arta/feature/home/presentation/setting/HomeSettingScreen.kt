package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeSettingScreen(
    vm: HomeSettingVM = viewModel(factory = HomeSettingVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        sessionName = uiState.sessionName,
        sessionEmail = uiState.sessionEmail,
        isDarkTheme = uiState.isDarkTheme,
        onToggleTheme = vm::onToggleTheme,
        onManageCategory = { backStack.add(CategoryRoute) },
        onClickManageWallet = { backStack.add(WalletRoute) },
        onLogout = {
            vm.onLogout()
            backStack.add(LoginRoute)
        },
    )
}

@Composable
private fun Content(
    sessionName: String,
    sessionEmail: String,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onManageCategory: () -> Unit,
    onClickManageWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Setting",
                style = MaterialTheme.typography.titleMedium,
            )

            Text(text = "Nama: $sessionName")
            Text(text = "Akun: $sessionEmail")

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tema gelap")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onToggleTheme,
                )
            }

            OutlinedButton(onClick = onManageCategory) {
                Text("Kelola kategori")
            }

            Button(onClick = onClickManageWallet) {
                Text("Kelola wallet")
            }

            OutlinedButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}

@Preview(showBackground = true, name = "Setting")
@Composable
private fun HomeSettingPreview() {
    ArtaTheme {
        Content(
            sessionName = "Rizal",
            sessionEmail = "rizal@example.com",
            isDarkTheme = true,
            onToggleTheme = {},
            onLogout = {},
            onManageCategory = {},
            onClickManageWallet = {},
        )
    }
}
