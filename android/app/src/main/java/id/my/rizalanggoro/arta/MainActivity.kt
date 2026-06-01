package id.my.rizalanggoro.arta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import id.my.rizalanggoro.arta.core.application.ComposeApp
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.utils.UpdateChecker
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authPrefs: AuthPrefs

    @Inject
    lateinit var selectedWalletPrefs: SelectedWalletPrefs

    @Inject
    lateinit var themePrefs: ThemePrefs

    @Inject
    lateinit var updateChecker: UpdateChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeApp(
                authPrefs = authPrefs,
                selectedWalletPrefs = selectedWalletPrefs,
                themePrefs = themePrefs
            )
        }
    }
}