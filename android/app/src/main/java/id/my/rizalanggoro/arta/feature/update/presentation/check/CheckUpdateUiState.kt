package id.my.rizalanggoro.arta.feature.update.presentation.check

data class CheckUpdateUiState(
    val currentVersionCode: Int = 0,
    val currentVersion: String = "",


    val isChecking: Boolean = false,
    val latestVersionCode: Int? = null,
    val latestUrl: String? = null,
    val isUpdateAvailable: Boolean = false,
    val statusMessage: String? = null,

    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0,
    val downloadedApkPath: String? = null,
    val errorMessage: String? = null,
)