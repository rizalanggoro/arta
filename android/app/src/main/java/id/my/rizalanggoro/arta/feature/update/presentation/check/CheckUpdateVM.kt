package id.my.rizalanggoro.arta.feature.update.presentation.check

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.receiver.InstallResultReceiver
import id.my.rizalanggoro.arta.openapi.apis.ReleaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CheckUpdateVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val releaseApi: ReleaseApi,
) : ViewModel() {
    private val packageInstaller = context.packageManager.packageInstaller
    private val _uiState = MutableStateFlow(CheckUpdateUiState())
    val uiState = _uiState.asStateFlow()

    fun onCheckClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isChecking = true,
                errorMessage = null,
                statusMessage = "Memeriksa pembaruan...",
            )
        }

        runCatching {
            val response = releaseApi.apiReleaseLatestGet()
            if (!response.isSuccessful) {
                throw IllegalStateException(response.errorMessage())
            }

            response.body()?.data
        }.onSuccess { release ->
            if (release == null) {
                _uiState.update {
                    it.copy(
                        isChecking = false,
                        isUpdateAvailable = false,
                        latestVersionCode = null,
                        latestUrl = null,
                        statusMessage = "Belum ada rilis terbaru",
                        downloadedApkPath = null,
                        isDownloading = false,
                        downloadProgress = 0,
                    )
                }
                return@onSuccess
            }

            _uiState.update {
                val isUpdateAvailable = release.versionCode > it.currentVersionCode

                it.copy(
                    latestVersionCode = release.versionCode,
                    latestUrl = release.url,
                    isUpdateAvailable = isUpdateAvailable,
                    isChecking = false,
                    isDownloading = false,
                    downloadedApkPath = null,
                    downloadProgress = 0,
                    statusMessage = when {
                        isUpdateAvailable -> "Pembaruan tersedia. Tekan unduh jika ingin memasang pembaruan."
                        else -> "Aplikasi sudah menggunakan versi terbaru"
                    },
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isChecking = false,
                    isDownloading = false,
                    downloadProgress = 0,
                    errorMessage = throwable.message ?: "Gagal memeriksa pembaruan",
                )
            }
        }
    }

    fun onDownloadClicked() {
        val latestUrl = _uiState.value.latestUrl ?: return
        val latestVersionCode = _uiState.value.latestVersionCode ?: return

        _uiState.update {
            it.copy(
                isDownloading = true,
                downloadProgress = 0,
                downloadedApkPath = null,
                errorMessage = null,
                statusMessage = "Mengunduh pembaruan...",
            )
        }

        downloadRelease(latestUrl, latestVersionCode)
    }

    fun onInstallClicked() {
        val apkPath = _uiState.value.downloadedApkPath ?: return
        val file = File(apkPath)

        if (!file.exists()) {
            _uiState.update {
                it.copy(
                    downloadedApkPath = null,
                    errorMessage = "File APK tidak ditemukan",
                )
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        statusMessage = "Menyiapkan pemasangan pembaruan...",
                    )
                }

                installWithPackageInstaller(file)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = throwable.message ?: "Gagal memasang pembaruan",
                        statusMessage = "Pemasangan gagal",
                    )
                }
            }
        }
    }

    private fun downloadRelease(url: String, versionCode: Int) {
        viewModelScope.launch {
            runCatching {
                val destination = File(context.cacheDir, "arta-release-$versionCode.apk")
                cleanupDownloadedApks(except = destination)
                downloadApk(url, destination) { progress ->
                    _uiState.update {
                        it.copy(
                            downloadProgress = progress,
                            statusMessage = "Mengunduh pembaruan... $progress%",
                        )
                    }
                }
            }.onSuccess { file ->
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        downloadProgress = 100,
                        downloadedApkPath = file.absolutePath,
                        statusMessage = "Unduhan selesai. Silakan instal pembaruan.",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = throwable.message ?: "Gagal mengunduh pembaruan",
                        statusMessage = "Unduhan gagal",
                    )
                }
            }
        }
    }

    private fun cleanupDownloadedApks(except: File? = null) {
        context.cacheDir.listFiles()
            ?.filter { file ->
                file.name.startsWith("arta-release-") && file.extension == "apk" && file != except
            }
            ?.forEach { file ->
                runCatching { file.delete() }
            }
    }

    private suspend fun downloadApk(
        url: String,
        destinationFile: File,
        onProgress: (Int) -> Unit,
    ): File = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Gagal mengunduh file pembaruan")
            }

            val body = response.body ?: throw IllegalStateException("Response body is null")
            val contentLength = body.contentLength().takeIf { it > 0 } ?: -1L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            body.byteStream().use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    var downloaded = 0L
                    while (true) {
                        val read = inputStream.read(buffer)
                        if (read <= 0) break

                        outputStream.write(buffer, 0, read)
                        downloaded += read

                        if (contentLength > 0) {
                            val progress =
                                ((downloaded * 100) / contentLength).toInt().coerceIn(0, 100)
                            onProgress(progress)
                        }
                    }
                    outputStream.flush()
                }
            }

            onProgress(100)
            destinationFile
        }
    }

    @SuppressLint("RequestInstallPackagesPolicy")
    private suspend fun installWithPackageInstaller(apkFile: File) = withContext(Dispatchers.IO) {
        val sessionParams = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        )
        val sessionId = packageInstaller.createSession(sessionParams)

        packageInstaller.openSession(sessionId).use { session ->
            apkFile.inputStream().use { inputStream ->
                session.openWrite(APK_SESSION_NAME, 0, apkFile.length()).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    session.fsync(outputStream)
                }
            }

            val callbackIntent = Intent(context, InstallResultReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                INSTALL_RESULT_REQUEST_CODE,
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )

            session.commit(pendingIntent.intentSender)
            session.close()
        }

        _uiState.update {
            it.copy(
                isDownloading = false,
                statusMessage = "Installer dibuka. Selesaikan konfirmasi pemasangan jika diminta.",
            )
        }
    }

    init {
        context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).let { packageInfo ->
            _uiState.update {
                it.copy(
                    currentVersionCode = PackageInfoCompat.getLongVersionCode(
                        packageInfo
                    ).toInt(),
                    currentVersion = packageInfo.versionName ?: "Unknown",
                )
            }
        }
    }

    private companion object {
        const val APK_SESSION_NAME = "arta-update.apk"
        const val INSTALL_RESULT_REQUEST_CODE = 1001
    }
}