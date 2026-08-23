package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun LatestGold(
    golds: List<DtoGold> = emptyList(),
    onClickEdit: (DomainGold) -> Unit = {},
    onClickDelete: (DomainGold) -> Unit = {},
) {
    var actionGold by remember { mutableStateOf<DomainGold?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallTitle(
            text = "Emas Terbaru",
            insideMargin = PaddingValues(top = 8.dp),
        )

        when {
            golds.isEmpty() -> EmptyPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )

            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    golds.forEach { gold ->
                        Box {
                            GoldListItem(
                                gold = gold,
                                onLongClick = { actionGold = it },
                            )

                            if (actionGold?.id == gold.data.id) {
                                WindowListPopup(
                                    show = true,
                                    onDismissRequest = { actionGold = null },
                                    alignment = PopupPositionProvider.Align.End,
                                ) {
                                    ListPopupColumn {
                                        listOf(
                                            DropdownItem(
                                                text = "Ubah",
                                                icon = { iconModifier ->
                                                    Icon(MiuixIcons.Edit, null, modifier = iconModifier)
                                                },
                                            ),
                                            DropdownItem(
                                                text = "Hapus",
                                                icon = { iconModifier ->
                                                    Icon(
                                                        MiuixIcons.Delete,
                                                        null,
                                                        modifier = iconModifier,
                                                        tint = MiuixTheme.colorScheme.error
                                                    )
                                                },
                                            ),
                                        ).forEachIndexed { index, item ->
                                            DropdownImpl(
                                                item = item,
                                                optionSize = 2,
                                                isSelected = false,
                                                index = index,
                                                onSelectedIndexChange = {
                                                    actionGold = null
                                                    when (index) {
                                                        0 -> onClickEdit(gold.data)
                                                        else -> onClickDelete(gold.data)
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "Tekan dan tahan untuk melihat opsi lainnya",
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    ArtaMiuixTheme {
        LatestGold(
            golds = List(3) {
                DtoGold(
                    data = DomainGold(
                        carat = 24.0,
                        createdAt = "2026-05-25T14:38:00.000+07:00",
                        date = "2026-05-25T14:38:00.000+07:00",
                        grams = 3.3,
                        id = 1,
                        notes = "",
                        price = 1500000.0,
                        type = "jewelry",
                        updatedAt = "2026-05-25T14:38:00.000+07:00",
                        walletId = 1
                    ),
                    profit = ((it - 1) * 500000.0),
                    sellPrice = (1500000 + ((it - 1) * 500000.0)),
                )
            }
        )
    }
}

@Composable
@Preview
private fun EmptyPreview() {
    ArtaMiuixTheme {
        LatestGold(
            golds = emptyList()
        )
    }
}
