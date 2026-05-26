package id.my.rizalanggoro.arta.feature.update.presentation.check

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun CheckUpdateScreen(vm: CheckUpdateVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onClickCheck = vm::onCheckClicked,
        onClickDownload = vm::onDownloadClicked,
        onClickInstall = vm::onInstallClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    uiState: CheckUpdateUiState = CheckUpdateUiState(),
    onClickCheck: () -> Unit = {},
    onClickDownload: () -> Unit = {},
    onClickInstall: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                },
                title = {
                    Text("Pembaruan")
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.clip(
                    RoundedCornerShape(16.dp)
                )
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Numbers,
                            null
                        )
                    },
                    headlineContent = {
                        Text("Versi aplikasi")
                    },
                    supportingContent = {
                        Text("${uiState.currentVersion}+${uiState.currentVersionCode}")
                    }
                )

                if (uiState.isUpdateAvailable)
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        leadingContent = {
                            Icon(
                                Icons.Rounded.Info,
                                null
                            )
                        },
                        headlineContent = {
                            Text("Pembaruan tersedia!")
                        }
                    )
            }

            when {
                uiState.isChecking -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }

                uiState.isDownloading -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        LinearWavyProgressIndicator(
                            progress = { uiState.downloadProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "Mengunduh ${uiState.downloadProgress}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                uiState.downloadedApkPath != null -> {
                    Button(
                        onClick = onClickInstall,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Instal pembaruan")
                    }
                }

                uiState.isUpdateAvailable -> {
                    Button(
                        onClick = onClickDownload,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Unduh pembaruan")
                    }
                }

                else -> {
                    Button(
                        onClick = onClickCheck,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Periksa pembaruan")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaTheme {
        Content(
            uiState = CheckUpdateUiState(
                currentVersion = "1.0",
                currentVersionCode = 1,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckingPreview() {
    ArtaTheme {
        Content(
            uiState = CheckUpdateUiState(
                isChecking = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateAvailablePreview() {
    ArtaTheme {
        Content(
            uiState = CheckUpdateUiState(
                currentVersionCode = 1,
                latestVersionCode = 2,
                isUpdateAvailable = true,
                statusMessage = "Pembaruan tersedia",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadingPreview() {
    ArtaTheme {
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
}

@Preview(showBackground = true)
@Composable
private fun NoUpdatePreview() {
    ArtaTheme {
        Content(
            uiState = CheckUpdateUiState(
                currentVersionCode = 2,
                latestVersionCode = 2,
                statusMessage = "Aplikasi sudah menggunakan versi terbaru",
            )
        )
    }
}