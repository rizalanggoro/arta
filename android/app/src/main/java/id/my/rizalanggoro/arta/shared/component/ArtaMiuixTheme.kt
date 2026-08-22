package id.my.rizalanggoro.arta.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/** Effective app-wide dark flag; provided by ComposeApp from ThemePrefs. */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun ArtaMiuixTheme(content: @Composable () -> Unit) {
    MiuixTheme(
        colors = if (LocalIsDarkTheme.current) darkColorScheme() else lightColorScheme()
    ) {
        content()
    }
}
