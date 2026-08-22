package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.AuthRoute
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.application.route.OtherRoute
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import top.yukonga.miuix.kmp.basic.Badge
import top.yukonga.miuix.kmp.basic.BadgedBox
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.icon.extended.Update
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun HomeSettingScreen(
    vm: HomeSettingVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onToggleTheme = vm::onToggleTheme,
        onClickManageCategory = { backStack.add(CategoryRoute.List) },
        onClickManageWallet = { backStack.add(WalletRoute.List) },
        onClickManageGoldTax = { backStack.add(GoldRoute.ListTax) },
        onClickUpdate = { backStack.add(OtherRoute.CheckUpdate) },
        onClickLogout = { backStack.add(AuthRoute.Logout) },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: HomeSettingUiState = HomeSettingUiState(),
    onToggleTheme: (Boolean) -> Unit = {},
    onClickManageWallet: () -> Unit = {},
    onClickManageCategory: () -> Unit = {},
    onClickManageGoldTax: () -> Unit = {},
    onClickUpdate: () -> Unit = {},
    onClickLogout: () -> Unit = {},
) {
    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SmallTitle(
                    text = "Akun",
                    insideMargin = PaddingValues(horizontal = 4.dp),
                )
            }
            item {
                Card {
                    BasicComponent(
                        title = uiState.session?.name ?: "Tidak diketahui",
                        summary = uiState.session?.email ?: "Tidak diketahui",
                        startAction = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MiuixTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            }
                        },
                    )
                }
            }

            item {
                SmallTitle(
                    text = "Preferensi",
                    insideMargin = PaddingValues(horizontal = 4.dp),
                )
            }
            item {
                Card {
                    BasicComponent(
                        title = "Tema gelap",
                        startAction = {
                            Icon(
                                MiuixIcons.Theme, null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        endActions = {
                            Switch(
                                checked = uiState.isDarkTheme,
                                onCheckedChange = onToggleTheme,
                            )
                        },
                        onClick = { onToggleTheme(!uiState.isDarkTheme) },
                    )
                }
            }

            item {
                SmallTitle(
                    text = "Pengelolaan",
                    insideMargin = PaddingValues(horizontal = 4.dp),
                )
            }
            item {
                Card {
                    BasicComponent(
                        title = "Dompet",
                        summary = "Kelola dompet tabungan uang dan emas",
                        startAction = {
                            Icon(
                                Icons.Rounded.Wallet, null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        endActions = {
                            Icon(MiuixIcons.Basic.ArrowRight, null)
                        },
                        onClick = onClickManageWallet,
                    )
                    BasicComponent(
                        title = "Kategori",
                        summary = "Kelola kategori pengeluaran dan pemasukan transaksi",
                        startAction = {
                            Icon(
                                Icons.Rounded.Category, null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        endActions = {
                            Icon(MiuixIcons.Basic.ArrowRight, null)
                        },
                        onClick = onClickManageCategory,
                    )
                    BasicComponent(
                        title = "Pajak emas",
                        summary = "Atur preferensi pajak jual emas berdasarkan ukuran karat",
                        startAction = {
                            Icon(
                                Icons.Rounded.Balance, null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        endActions = {
                            Icon(MiuixIcons.Basic.ArrowRight, null)
                        },
                        onClick = onClickManageGoldTax,
                    )
                }
            }

            item {
                SmallTitle(
                    text = "Lainnya",
                    insideMargin = PaddingValues(horizontal = 4.dp),
                )
            }
            item {
                Card {
                    BasicComponent(
                        title = "Pembaruan",
                        summary = "Periksa dan unduh pembaruan aplikasi",
                        startAction = {
                            BadgedBox(
                                badge = {
                                    if (uiState.hasUpdate) Badge()
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(MiuixIcons.Update, null)
                            }
                        },
                        endActions = {                            Icon(MiuixIcons.Basic.ArrowRight, null)
                        },
                        onClick = onClickUpdate,
                    )
                    BasicComponent(
                        title = "Keluar",
                        startAction = {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout, null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        endActions = {
                            Icon(MiuixIcons.Basic.ArrowRight, null)
                        },
                        onClick = onClickLogout,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSettingPreview() {
    Content(
        uiState = HomeSettingUiState(
            isDarkTheme = true
        )
    )
}
