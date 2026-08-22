package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.HomeRoute
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ErrorColor = Color(0xFFE53935)

@Composable
fun CreateFirstWalletScreen(vm: CreateFirstWalletVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<CreateFirstWalletUiState.Event.ShowMessage>()
            .collect { snackbarHostState.showSnackbar(it.message) }
    }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<CreateFirstWalletUiState.Event.CreateSucceeded>()
            .collect {
                backStack.clear()
                backStack.add(HomeRoute.Index)
            }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::create,
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: CreateFirstWalletUiState = CreateFirstWalletUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(title = "Buat Dompet Pertama")
        },
        snackbarHost = { SnackbarHost(state = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextField(
                    value = uiState.name,
                    onValueChange = onChangeName,
                    label = "Nama dompet",
                    useLabelAsPlaceholder = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    colors = if (uiState.nameError != null) {
                        TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                    } else {
                        TextFieldDefaults.textFieldColors()
                    },
                )
                uiState.nameError?.let { error ->
                    Text(error, fontSize = 13.sp, color = ErrorColor)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    "Tipe wallet",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TabRowWithContour(
                    tabs = walletTypes.map { it.name },
                    selectedTabIndex = walletTypes
                        .indexOfFirst { it.value == uiState.type }
                        .coerceAtLeast(0),
                    onTabSelected = { index -> onChangeType(walletTypes[index].value) },
                )
                uiState.typeError?.let { error ->
                    Text(text = error, color = MiuixTheme.colorScheme.error, fontSize = 13.sp)
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                }

                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColorsPrimary()
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = CreateFirstWalletUiState(
                type = "cash_savings"
            )
        )
    }
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletLoadingPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = CreateFirstWalletUiState(
                type = "cash_savings",
                isLoading = true
            )
        )
    }
}
