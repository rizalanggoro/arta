package id.my.rizalanggoro.arta.core.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedWalletPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("selected_wallet_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val _selectedWallet = MutableStateFlow(getSelectedWallet())
    val selectedWallet: StateFlow<DomainWallet?> = _selectedWallet.asStateFlow()

    fun saveSelectedWallet(wallet: DomainWallet) {
        prefs.edit()
            .putString(KEY_SELECTED_WALLET, json.encodeToString(DomainWallet.serializer(), wallet))
            .apply()
        _selectedWallet.value = wallet
    }

    fun clear() {
        prefs.edit().remove(KEY_SELECTED_WALLET).apply()
        _selectedWallet.value = null
    }

    private fun getSelectedWallet(): DomainWallet? {
        val raw = prefs.getString(KEY_SELECTED_WALLET, null) ?: return null
        return runCatching {
            json.decodeFromString(DomainWallet.serializer(), raw)
        }.getOrNull()
    }

    companion object {
        private const val KEY_SELECTED_WALLET = "selected_wallet"
    }
}
