package id.my.rizalanggoro.arta.feature.update.presentation.check

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun CheckUpdateScreen(vm: CheckUpdateVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCheck = vm::onCheckClicked,
        onClickDownload = vm::onDownloadClicked,
        onClickInstall = vm::onInstallClicked,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: CheckUpdateUiState = CheckUpdateUiState(),
    onClickCheck: () -> Unit = {},
    onClickDownload: () -> Unit = {},
    onClickInstall: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Pembaruan",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                MiuixIcons.Back,
                                null
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                ) {
                    BasicComponent(
                        title = "Versi aplikasi",
                        summary = "${uiState.currentVersion}+${uiState.currentVersionCode}",
                    )

                    if (uiState.isUpdateAvailable)
                        BasicComponent(
                            title = "Pembaruan tersedia!",
                            startAction = {
                                Icon(
                                    MiuixIcons.Info, null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                        )
                }

                when {
                    uiState.isChecking -> Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                    }

                    uiState.isDownloading -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = uiState.downloadProgress / 100f,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text = "Mengunduh ${uiState.downloadProgress}%",
                                fontSize = 13.sp,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }

                    uiState.downloadedApkPath != null -> {
                        Button(
                            onClick = onClickInstall,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text("Instal pembaruan")
                        }
                    }

                    uiState.isUpdateAvailable -> {
                        Button(
                            onClick = onClickDownload,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text("Unduh pembaruan")
                        }
                    }

                    else -> {
                        Button(
                            onClick = onClickCheck,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text("Periksa pembaruan")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Content(
        uiState = CheckUpdateUiState(
            currentVersion = "1.0",
            currentVersionCode = 1,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckingPreview() {
    Content(
        uiState = CheckUpdateUiState(
            isChecking = true
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun UpdateAvailablePreview() {
    Content(
        uiState = CheckUpdateUiState(
            currentVersionCode = 1,
            latestVersionCode = 2,
            isUpdateAvailable = true,
            statusMessage = "Pembaruan tersedia",
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DownloadingPreview() {
    Content(
        uiState = CheckUpdateUiState(
            currentVersionCode = 1,
            latestVersionCode = 2,
            isDownloading = true,
            downloadProgress = 62,
            statusMessage = "Mengunduh pembaruan...",
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun NoUpdatePreview() {
    Content(
        uiState = CheckUpdateUiState(
            currentVersionCode = 2,
            latestVersionCode = 2,
            statusMessage = "Aplikasi sudah menggunakan versi terbaru",
        )
    )
}
