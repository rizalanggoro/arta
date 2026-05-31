package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionActionSheet(
    transactionId: Int = 0
) {
    val backStack = LocalBackStack.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable {
                        scope.launch {
                            backStack.removeLastOrNull()
                            AppEventBus.emit(
                                AppEvent.TransactionActionSheet.OnEditClicked(
                                    transactionId = transactionId,
                                )
                            )
                        }
                    },
                leadingContent = {
                    Icon(
                        Icons.Rounded.Edit,
                        null
                    )
                },
                headlineContent = {
                    Text("Ubah")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                }
            )
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp,
                        )
                    )
                    .clickable {
                        scope.launch {
                            AppEventBus.emit(
                                AppEvent.TransactionActionSheet.OnDeleteClicked(
                                    transactionId = transactionId
                                )
                            )
                            backStack.removeLastOrNull()
                        }
                    },
                leadingContent = {
                    Icon(
                        Icons.Rounded.Delete,
                        null
                    )
                },
                headlineContent = {
                    Text("Hapus")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                }
            )
        }
        FilledTonalButton(
            onClick = {
                backStack.removeLastOrNull()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Batal")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaTheme {
        TransactionActionSheet()
    }
}