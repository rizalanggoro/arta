package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import id.my.rizalanggoro.arta.domain.Wallet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object WalletSelectionBus {
    private val _selectedWallet = MutableSharedFlow<Wallet>(extraBufferCapacity = 1)
    val selectedWallet: SharedFlow<Wallet> = _selectedWallet.asSharedFlow()

    suspend fun emit(wallet: Wallet) {
        _selectedWallet.emit(wallet)
    }
}