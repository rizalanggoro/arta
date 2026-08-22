package id.my.rizalanggoro.arta.feature.transaction.presentation.action

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.constant.transactionGroups
import id.my.rizalanggoro.arta.core.constant.transactionTimeRanges
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text

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
                        fontSize = 16.sp
                    )
                    TabRowWithContour(
                        tabs = transactionGroups.map { it.title },
                        selectedTabIndex = transactionGroups
                            .indexOfFirst { it.value == groupBy }
                            .coerceAtLeast(0),
                        onTabSelected = { index ->
                            onChangeGroupBy(transactionGroups[index].value)
                        },
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Lihat dalam rentang",
                        fontSize = 16.sp
                    )
                    TabRowWithContour(
                        tabs = transactionTimeRanges.map { it.title },
                        selectedTabIndex = transactionTimeRanges
                            .indexOfFirst { it.value == timeRange }
                            .coerceAtLeast(0),
                        onTabSelected = { index ->
                            onChangeTimeRange(transactionTimeRanges[index].value)
                        },
                    )
                }
            }
            Button(
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
    ArtaMiuixTheme {
        Content()
    }
}
