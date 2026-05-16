package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeWalletType

@Composable
fun HomeSettingContent(
    walletType: HomeWalletType,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onToggleTheme: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
    onManageCategory: () -> Unit = {},
    onClickManageWallet: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext as? MyApplication
    val session = context?.authPrefs?.currentSession?.value

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Setting",
                style = MaterialTheme.typography.titleMedium,
            )

            // user info
            Text(text = "Nama: ${session?.name ?: "-"}")
            Text(text = "Akun: ${session?.email ?: "-"}")

            // theme toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tema gelap")
                Spacer(modifier = Modifier.width(8.dp))
                val checked by remember { mutableStateOf(isDarkTheme) }
                Switch(
                    checked = checked,
                    onCheckedChange = { checkedNow -> onToggleTheme(checkedNow) },
                )
            }

            // navigation options
            OutlinedButton(onClick = onManageCategory) {
                Text("Kelola kategori")
            }

            Button(onClick = onClickManageWallet) {
                Text("Kelola wallet")
            }

            // logout
            OutlinedButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}

@Preview(showBackground = true, name = "Setting - Cash")
@Composable
private fun HomeSettingCashPreview() {
    ArtaTheme {
        HomeSettingContent(walletType = HomeWalletType.CashSavings)
    }
}

@Preview(showBackground = true, name = "Setting - Gold")
@Composable
private fun HomeSettingGoldPreview() {
    ArtaTheme {
        HomeSettingContent(walletType = HomeWalletType.GoldSavings)
    }
}
