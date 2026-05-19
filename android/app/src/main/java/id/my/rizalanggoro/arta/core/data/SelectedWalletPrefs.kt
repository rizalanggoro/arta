package id.my.rizalanggoro.arta.core.data

import android.content.Context
import id.my.rizalanggoro.arta.domain.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SelectedWalletPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("selected_wallet_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val _selectedWallet = MutableStateFlow(getSelectedWallet())
    val selectedWallet: StateFlow<Wallet?> = _selectedWallet.asStateFlow()

    fun saveSelectedWallet(wallet: Wallet) {
        prefs.edit()
            .putString(KEY_SELECTED_WALLET, json.encodeToString(Wallet.serializer(), wallet))
            .apply()
        _selectedWallet.value = wallet
    }

    fun clear() {
        prefs.edit().remove(KEY_SELECTED_WALLET).apply()
        _selectedWallet.value = null
    }

    private fun getSelectedWallet(): Wallet? {
        val raw = prefs.getString(KEY_SELECTED_WALLET, null) ?: return null
        return runCatching {
            json.decodeFromString(Wallet.serializer(), raw)
        }.getOrNull()
    }

    companion object {
        private const val KEY_SELECTED_WALLET = "selected_wallet"
    }
}
