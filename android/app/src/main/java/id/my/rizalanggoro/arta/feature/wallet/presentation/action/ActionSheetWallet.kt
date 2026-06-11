package id.my.rizalanggoro.arta.feature.wallet.presentation.action

import androidx.compose.runtime.Composable
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ActionSheet

@Composable
fun ActionSheetWallet(
    walletId: Int,
) {
    val backStack = LocalBackStack.current

    ActionSheet(
        onClickEdit = {
            backStack.apply {
                removeLastOrNull()
                add(
                    WalletRoute.Upsert(
                        walletId = walletId
                    )
                )
            }
        },
        onClickDelete = {
            backStack.apply {
                removeLastOrNull()
                add(
                    WalletRoute.Delete(
                        walletId = walletId
                    )
                )
            }
        },
        onClickCancel = {
            backStack.removeLastOrNull()
        }
    )
}