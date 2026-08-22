package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CategoryActionSheet(categoryId: Int = 0) {
    val backStack = LocalBackStack.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ActionRow(
                title = "Ubah",
                icon = Icons.Rounded.Edit,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                ),
                onClick = {
                    scope.launch {
                        AppEventBus.emit(
                            AppEvent.CategoryActionSheet.OnEditClicked(
                                categoryId = categoryId,
                            )
                        )
                    }.invokeOnCompletion {
                        backStack.removeLastOrNull()
                    }
                }
            )
            ActionRow(
                title = "Hapus",
                icon = Icons.Rounded.Delete,
                iconTint = MiuixTheme.colorScheme.error,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp,
                ),
                onClick = {
                    scope.launch {
                        AppEventBus.emit(
                            AppEvent.CategoryActionSheet.OnDeleteClicked(
                                categoryId = categoryId
                            )
                        )
                    }.invokeOnCompletion {
                        backStack.removeLastOrNull()
                    }
                }
            )
        }
        Button(
            onClick = { backStack.removeLastOrNull() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Batal")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    CompositionLocalProvider(LocalBackStack provides rememberNavBackStack()) {
        ArtaMiuixTheme {
            CategoryActionSheet()
        }
    }
}
