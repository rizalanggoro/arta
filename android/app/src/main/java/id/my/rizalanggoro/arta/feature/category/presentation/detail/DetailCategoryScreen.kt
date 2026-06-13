package id.my.rizalanggoro.arta.feature.category.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.utils.Samples
import id.my.rizalanggoro.arta.shared.component.TransactionListItem

@Composable
fun DetailCategoryScreen(
    vm: DetailCategoryVM,
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onClickBack = backStack::removeLastOrNull,
        onRefresh = { vm.getCategory(isRefresh = true) }
    )
}

@Composable
private fun Content(
    uiState: DetailCategoryUiState = DetailCategoryUiState(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    onClickBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                },
                title = {
                    Text("Detail Kategori")
                }
            )
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(it),
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            when {
                                uiState.isLoading -> 4.dp
                                else -> 2.dp
                            }
                        )
                    ) {
                        Text(
                            uiState.category?.data?.name ?: "Loading category",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.then(
                                when {
                                    uiState.isLoading -> Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant)

                                    else -> Modifier
                                }
                            ),
                            color = when {
                                uiState.isLoading -> Color.Transparent
                                else -> Color.Unspecified
                            }
                        )
                        Text(
                            "Berikut total pemasukan dan daftar transaksi yang dilakukan selama " +
                                    "satu hari, yaitu Senin, 12 Juni 2024",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                uiState.isLoading -> Color.Transparent
                                else -> MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.then(
                                when {
                                    uiState.isLoading -> Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant)

                                    else -> Modifier
                                }
                            ),
                            maxLines = when {
                                uiState.isLoading -> 1
                                else -> Int.MAX_VALUE
                            }
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .then(
                                when {
                                    uiState.isLoading -> Modifier.shimmer()
                                    else -> Modifier
                                }
                            ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                val isIncome = uiState.category?.data?.type == "income"

                                Icon(
                                    when (isIncome) {
                                        true -> Icons.AutoMirrored.Rounded.CallReceived
                                        else -> Icons.AutoMirrored.Rounded.CallMade
                                    },
                                    null,
                                    tint = when {
                                        uiState.isLoading -> Color.Transparent
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier
                                        .size(16.dp)
                                        .then(
                                            when {
                                                uiState.isLoading -> Modifier.clip(
                                                    RoundedCornerShape(4.dp)
                                                )

                                                else -> Modifier
                                            }
                                        )
                                        .background(
                                            when {
                                                uiState.isLoading -> MaterialTheme.colorScheme.outlineVariant
                                                else -> Color.Unspecified
                                            }
                                        )
                                )
                                Text(
                                    text = when (isIncome) {
                                        true -> "Pemasukan"
                                        else -> "Pengeluaran"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        uiState.isLoading -> Color.Transparent
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .then(
                                            when {
                                                uiState.isLoading -> Modifier.clip(
                                                    RoundedCornerShape(4.dp)
                                                )

                                                else -> Modifier
                                            }
                                        )
                                        .background(
                                            when {
                                                uiState.isLoading -> MaterialTheme.colorScheme.outlineVariant
                                                else -> Color.Unspecified
                                            }
                                        )
                                )
                            }
                            Text(
                                text = (uiState.category?.totalAmount
                                    ?: 0.0).toIndonesianCurrency(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    uiState.isLoading -> Color.Transparent
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                },
                                modifier = Modifier
                                    .then(
                                        when {
                                            uiState.isLoading -> Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))

                                            else -> Modifier
                                        }
                                    )
                                    .background(
                                        when {
                                            uiState.isLoading -> MaterialTheme.colorScheme.outlineVariant
                                            else -> Color.Unspecified
                                        }
                                    )
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Daftar transaksi",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp)
                            .then(
                                when {
                                    uiState.isLoading -> Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant)

                                    else -> Modifier
                                }
                            ),
                        color = when {
                            uiState.isLoading -> Color.Transparent
                            else -> Color.Unspecified
                        }
                    )
                }

                when {
                    uiState.isLoading -> items(3) { index ->
                        TransactionListItem(
                            isLoading = true,
                            index = index,
                            size = 3,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(
                                    top = when {
                                        index == 0 -> 16.dp
                                        else -> 2.dp
                                    }
                                ),
                            transaction = Samples.dtoTransactions[index]
                        )
                    }

                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    Content()
}

@Composable
@Preview
private fun LoadingPreview() {
    Content(
        uiState = DetailCategoryUiState(
            isLoading = true
        )
    )
}