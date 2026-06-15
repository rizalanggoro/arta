package id.my.rizalanggoro.arta.feature.transaction.presentation.action

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.constant.transactionGroups
import id.my.rizalanggoro.arta.core.constant.transactionTimeRanges
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterActionSheet(
    vm: TransactionFilterVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onChangeGroupBy = vm::onGroupByChanged,
        onChangeTimeRange = vm::onTimeRangeChanged,
        onClickDone = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    uiState: TransactionFilterUiState = TransactionFilterUiState(),
    onChangeGroupBy: (TransactionGroupType) -> Unit = {},
    onChangeTimeRange: (TransactionTimeRangeType) -> Unit = {},
    onClickDone: () -> Unit = {},
) {
    with(uiState) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Lihat berdasarkan",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        transactionGroups.forEachIndexed { index, item ->
                            ToggleButton(
                                checked = groupBy == item.value,
                                onCheckedChange = { onChangeGroupBy(item.value) },
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    item.title,
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Lihat dalam rentang",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        transactionTimeRanges.forEachIndexed { index, item ->
                            ToggleButton(
                                checked = timeRange == item.value,
                                onCheckedChange = { onChangeTimeRange(item.value) },
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    2 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    item.title,
                                )
                            }
                        }
                    }
                }
            }
            FilledTonalButton(
                onClick = onClickDone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selesai")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaTheme {
        Content()
    }
}