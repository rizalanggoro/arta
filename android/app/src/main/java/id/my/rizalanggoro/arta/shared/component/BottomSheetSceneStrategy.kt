package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy.Companion.bottomSheet
import top.yukonga.miuix.kmp.window.WindowBottomSheet
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/** Set by screens inside the sheet to show a title in the sheet's built-in title row. */
val LocalBottomSheetTitle = staticCompositionLocalOf<MutableState<String?>?> { null }

/** An [OverlayScene] that renders an [entry] within a [WindowBottomSheet]. */
internal data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val lifecycleOwner = rememberLifecycleOwner()
        var visible by remember { mutableStateOf(true) }
        val sheetTitle = remember { mutableStateOf<String?>(null) }
        val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
        MiuixTheme(colors = colors) {
            CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
                WindowBottomSheet(
                    show = visible,
                    title = sheetTitle.value,
                    onDismissRequest = { visible = false },
                    onDismissFinished = onBack,
                ) {
                    CompositionLocalProvider(
                        LocalBottomSheetTitle provides sheetTitle,
                        LocalLifecycleOwner provides lifecycleOwner,
                    ) {
                        entry.Content()
                    }
                }
            }
        }
    }
}

/**
 * A [SceneStrategy] that displays entries that have added [bottomSheet] to their [NavEntry.metadata]
 * within a [WindowBottomSheet] instance.
 *
 * This strategy should always be added before any non-overlay scene strategies.
 */
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        lastEntry.metadata[BottomSheetKey] ?: return null
        return BottomSheetScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            onBack = onBack
        )
    }

    companion object {
        /**
         * Function to be called on the [NavEntry.metadata] to mark this entry as something that
         * should be displayed within a [WindowBottomSheet].
         */
        fun bottomSheet() = metadata {
            put(BottomSheetKey, Unit)
        }

        object BottomSheetKey : NavMetadataKey<Unit>
    }

}
