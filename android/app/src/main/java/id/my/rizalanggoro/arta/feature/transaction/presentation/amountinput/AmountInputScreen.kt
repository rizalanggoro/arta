package id.my.rizalanggoro.arta.feature.transaction.presentation.amountinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AmountInputScreen(
    vm: AmountInputVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val scope = rememberCoroutineScope()

    Content(
        uiState = uiState,
        onInputDigit = vm::inputDigit,
        onInputDot = vm::inputDot,
        onInputOperator = vm::inputOperator,
        onInputOpenParen = vm::inputOpenParen,
        onInputCloseParen = vm::inputCloseParen,
        onBackspace = vm::backspace,
        onClear = vm::clear,
        onClickBack = { backStack.removeLastOrNull() },
        onClickConfirm = {
            scope.launch {
                if (vm.confirm()) backStack.removeLastOrNull()
            }
        },
    )
}

@Composable
private fun Content(
    uiState: AmountInputUiState = AmountInputUiState(),
    onInputDigit: (Char) -> Unit = {},
    onInputDot: () -> Unit = {},
    onInputOperator: (Char) -> Unit = {},
    onInputOpenParen: () -> Unit = {},
    onInputCloseParen: () -> Unit = {},
    onBackspace: () -> Unit = {},
    onClear: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickConfirm: () -> Unit = {},
) {
    ArtaMiuixTheme {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Nominal Transaksi",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(MiuixIcons.Back, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onClickConfirm,
                            enabled = uiState.canConfirm,
                        ) {
                            Icon(MiuixIcons.Ok, null)
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.Bottom),
                ) {
                    Text(
                        text = uiState.expression.pretty().ifEmpty { "0" },
                        style = MiuixTheme.textStyles.title1.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.End,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = uiState.result?.toIndonesianCurrency()?.let { "= $it" } ?: "-",
                        style = MiuixTheme.textStyles.body1,
                        textAlign = TextAlign.End,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val keys: List<List<CalcKey>> = listOf(
                        listOf(
                            CalcKey.Label("C", onClick = onClear),
                            CalcKey.Label("(", onClick = onInputOpenParen),
                            CalcKey.Label(")", onClick = onInputCloseParen),
                            CalcKey.Operator("÷", '/', onClick = onInputOperator),
                        ),
                        listOf(
                            CalcKey.Digit('7', onClick = onInputDigit),
                            CalcKey.Digit('8', onClick = onInputDigit),
                            CalcKey.Digit('9', onClick = onInputDigit),
                            CalcKey.Operator("×", '*', onClick = onInputOperator),
                        ),
                        listOf(
                            CalcKey.Digit('4', onClick = onInputDigit),
                            CalcKey.Digit('5', onClick = onInputDigit),
                            CalcKey.Digit('6', onClick = onInputDigit),
                            CalcKey.Operator("−", '-', onClick = onInputOperator),
                        ),
                        listOf(
                            CalcKey.Digit('1', onClick = onInputDigit),
                            CalcKey.Digit('2', onClick = onInputDigit),
                            CalcKey.Digit('3', onClick = onInputDigit),
                            CalcKey.Operator("+", '+', onClick = onInputOperator),
                        ),
                        listOf(
                            CalcKey.Label(".", onClick = onInputDot),
                            CalcKey.Digit('0', onClick = onInputDigit),
                            CalcKey.Label("⌫", onClick = onBackspace),
                            CalcKey.Equals,
                        ),
                    )
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    is CalcKey.Digit -> PadButton(
                                        label = key.char.toString(),
                                        onClick = { key.onClick(key.char) },
                                        modifier = Modifier.weight(1f),
                                    )

                                    is CalcKey.Operator -> PadButton(
                                        label = key.label,
                                        onClick = { key.onClick(key.value) },
                                        modifier = Modifier.weight(1f),
                                    )

                                    is CalcKey.Label -> PadButton(
                                        label = key.label,
                                        onClick = key.onClick,
                                        modifier = Modifier.weight(1f),
                                    )

                                    CalcKey.Equals -> Button(
                                        onClick = onClickConfirm,
                                        enabled = uiState.canConfirm,
                                        colors = ButtonDefaults.buttonColorsPrimary(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp),
                                    ) {
                                        Text("=", style = MiuixTheme.textStyles.title2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed interface CalcKey {
    data class Digit(val char: Char, val onClick: (Char) -> Unit) : CalcKey
    data class Operator(val label: String, val value: Char, val onClick: (Char) -> Unit) : CalcKey
    data class Label(val label: String, val onClick: () -> Unit) : CalcKey
    data object Equals : CalcKey
}

@Composable
private fun PadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
    ) {
        Text(label, style = MiuixTheme.textStyles.title2)
    }
}

private fun String.pretty(): String = replace("*", "×").replace("/", "÷").replace("-", "−")

@Preview(showBackground = true, name = "Kalkulator Nominal")
@Composable
private fun AmountInputPreview() {
    Content(
        uiState = AmountInputUiState(
            expression = "50000+25000",
            result = 75000.0,
            canConfirm = true,
        ),
    )
}

@Preview(showBackground = true, name = "Kalkulator Nominal - Kosong")
@Composable
private fun AmountInputEmptyPreview() {
    Content()
}
