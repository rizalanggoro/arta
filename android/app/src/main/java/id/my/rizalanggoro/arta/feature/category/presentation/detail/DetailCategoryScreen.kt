package id.my.rizalanggoro.arta.feature.category.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
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
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.utils.Samples
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.shared.component.TransactionListItem
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun DetailCategoryScreen(
    vm: DetailCategoryVM,
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onClickBack = backStack::removeLastOrNull,
        onRefresh = { vm.getCategory() },
        onClickTransaction = {
            backStack.add(
                TransactionRoute.Detail(transactionId = it.id)
            )
        },
        onLongClickTransaction = {
            backStack.add(
                TransactionRoute.ActionSheet(
                    transactionId = it.id
                )
            )
        }
    )
}

@Composable
private fun Content(
    uiState: DetailCategoryUiState = DetailCategoryUiState(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    onClickBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onClickTransaction: (DomainTransaction) -> Unit = {},
    onLongClickTransaction: (DomainTransaction) -> Unit = {},
) {
    with(uiState) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Detail Kategori",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                MiuixIcons.Back,
                                null
                            )
                        }
                    }
                )
            }
        ) {
            PullToRefresh(
                modifier = Modifier.padding(it),
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                pullToRefreshState = pullToRefreshState
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                ) {
                    item {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                when {
                                    uiState.isLoading -> 4.dp
                                    else -> 2.dp
                                }
                            )
                        ) {
                            Text(
                                uiState.category?.data?.name ?: "Loading category",
                                style = MiuixTheme.textStyles.title2,
                                modifier = Modifier.then(
                                    when {
                                        uiState.isLoading -> Modifier
                                            .shimmer()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MiuixTheme.colorScheme.outline)

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
                                style = MiuixTheme.textStyles.body2,
                                color = when {
                                    uiState.isLoading -> Color.Transparent
                                    else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                                },
                                modifier = Modifier.then(
                                    when {
                                        uiState.isLoading -> Modifier
                                            .shimmer()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MiuixTheme.colorScheme.outline)

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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .then(
                                    when {
                                        uiState.isLoading -> Modifier.shimmer()
                                        else -> Modifier
                                    }
                                ),
                            colors = CardDefaults.defaultColors(
                                color = MiuixTheme.colorScheme.secondaryContainer,
                                contentColor = MiuixTheme.colorScheme.onSecondaryContainer,
                            ),
                            insideMargin = PaddingValues(16.dp),
                        ) {
                            Column(
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
                                            else -> MiuixTheme.colorScheme.primary
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
                                                    uiState.isLoading -> MiuixTheme.colorScheme.outline
                                                    else -> Color.Unspecified
                                                }
                                            )
                                    )
                                    Text(
                                        text = when (isIncome) {
                                            true -> "Pemasukan"
                                            else -> "Pengeluaran"
                                        },
                                        style = MiuixTheme.textStyles.footnote1,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when {
                                            uiState.isLoading -> Color.Transparent
                                            else -> MiuixTheme.colorScheme.primary
                                        },
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
                                                    uiState.isLoading -> MiuixTheme.colorScheme.outline
                                                    else -> Color.Unspecified
                                                }
                                            )
                                    )
                                }
                                Text(
                                    text = (uiState.category?.totalAmount
                                        ?: 0.0).toIndonesianCurrency(),
                                    style = MiuixTheme.textStyles.body1,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        uiState.isLoading -> Color.Transparent
                                        else -> MiuixTheme.colorScheme.onBackground
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
                                                uiState.isLoading -> MiuixTheme.colorScheme.outline
                                                else -> Color.Unspecified
                                            }
                                        )
                                )
                            }
                        }
                    }

                    item {
                        SmallTitle(
                            text = "Daftar transaksi",
                            modifier = Modifier.padding(top = 24.dp),
                            insideMargin = PaddingValues(vertical = 8.dp),
                        )
                    }

                    when {
                        uiState.isLoading -> items(3) { index ->
                            TransactionListItem(
                                isLoading = true,
                                index = index,
                                size = 3,
                                modifier = Modifier.padding(
                                    top = when {
                                        index == 0 -> 16.dp
                                        else -> 2.dp
                                    }
                                ),
                                transaction = Samples.domainTransactions.first(),
                                category = Samples.domainCategories.first()
                            )
                        }

                        category != null -> itemsIndexed(
                            category.transactions ?: emptyList()
                        ) { index, transaction ->
                            TransactionListItem(
                                modifier = Modifier.padding(
                                    top = when {
                                        index == 0 -> 16.dp
                                        else -> 2.dp
                                    }
                                ),
                                transaction = transaction,
                                category = category.data,
                                index = index,
                                size = category.transactions?.size ?: 0,
                                onClick = onClickTransaction,
                                onLongClick = {
                                    onLongClickTransaction(transaction)
                                },
                            )
                        }
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
