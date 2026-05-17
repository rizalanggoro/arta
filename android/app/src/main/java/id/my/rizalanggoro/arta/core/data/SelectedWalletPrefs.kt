package id.my.rizalanggoro.arta.core.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectedWalletPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("selected_wallet_prefs", Context.MODE_PRIVATE)

    private val _selectedWalletId = MutableStateFlow<Int?>(getSelectedId())
    val selectedWalletId: StateFlow<Int?> = _selectedWalletId.asStateFlow()

    fun saveSelectedWalletId(id: Int) {
        prefs.edit().putInt(KEY_SELECTED_WALLET_ID, id).apply()
        _selectedWalletId.value = id
    }

    fun clear() {
        prefs.edit().remove(KEY_SELECTED_WALLET_ID).apply()
        _selectedWalletId.value = null
    }

    private fun getSelectedId(): Int? {
        return if (prefs.contains(KEY_SELECTED_WALLET_ID)) prefs.getInt(KEY_SELECTED_WALLET_ID, -1).takeIf { it >= 0 } else null
    }

    companion object {
        private const val KEY_SELECTED_WALLET_ID = "selected_wallet_id"
    }
}
