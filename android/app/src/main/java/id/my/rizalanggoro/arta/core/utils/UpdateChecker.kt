package id.my.rizalanggoro.arta.core.utils

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.ReleaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateChecker @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val releaseApi: ReleaseApi
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )

            val currentVersionCode = PackageInfoCompat.getLongVersionCode(
                packageInfo
            ).toInt()

            runCatching {
                val response = releaseApi.apiReleaseLatestGet()
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Terjadi kesalahan tak terduga")
            }.onSuccess { response ->
                if (response.data.versionCode > currentVersionCode) {
                    AppEventBus.emitUpdate()
                }
            }
        }
    }
}