package id.my.rizalanggoro.arta.feature.transaction.presentation.amountinput

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

@HiltViewModel(assistedFactory = AmountInputVM.Factory::class)
class AmountInputVM @AssistedInject constructor(
    @Assisted private val navKey: TransactionRoute.AmountInput,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: TransactionRoute.AmountInput): AmountInputVM
    }

    private val _uiState = MutableStateFlow(AmountInputUiState(expression = navKey.amount))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun inputDigit(digit: Char) {
        _uiState.update { it.copy(expression = it.expression + digit) }
        refresh()
    }

    fun inputDot() {
        val expression = _uiState.value.expression
        val segmentStart = expression.indexOfLast { it in "+-*/(" } + 1
        val segment = expression.substring(segmentStart)
        if ('.' in segment) return
        _uiState.update { it.copy(expression = expression + if (segment.isEmpty()) "0." else ".") }
        refresh()
    }

    fun inputOperator(operator: Char) {
        val expression = _uiState.value.expression
        if (expression.isEmpty()) {
            if (operator == '-') _uiState.update { it.copy(expression = "-") }
            refresh()
            return
        }
        val updated = when (val last = expression.last()) {
            in '0'..'9', '.', ')' -> expression + operator
            '(' -> if (operator == '-') expression + operator else expression
            else -> expression.dropLast(1) + operator
        }
        _uiState.update { it.copy(expression = updated) }
        refresh()
    }

    fun inputOpenParen() {
        val expression = _uiState.value.expression
        val updated = when {
            expression.isEmpty() || expression.last() in "+-*/(" -> expression + "("
            expression.last().isDigit() || expression.last() == ')' -> expression + "*("
            else -> expression
        }
        _uiState.update { it.copy(expression = updated) }
        refresh()
    }

    fun inputCloseParen() {
        val expression = _uiState.value.expression
        if (expression.isEmpty()) return
        val open = expression.count { it == '(' }
        val close = expression.count { it == ')' }
        if (open > close && (expression.last().isDigit() || expression.last() == ')')) {
            _uiState.update { it.copy(expression = expression + ")") }
            refresh()
        }
    }

    fun backspace() {
        _uiState.update { it.copy(expression = it.expression.dropLast(1)) }
        refresh()
    }

    fun clear() {
        _uiState.update { it.copy(expression = "") }
        refresh()
    }

    suspend fun confirm(): Boolean {
        val result = _uiState.value.result ?: return false
        if (!result.isFinite() || result <= 0) return false
        AppEventBus.emit(AppEvent.AmountConfirmed(result.normalize()))
        return true
    }

    private fun refresh() {
        val result = _uiState.value.expression
            .takeIf { it.isNotBlank() }
            ?.let(::evaluate)
            ?.takeIf { it.isFinite() }
        _uiState.update {
            it.copy(
                result = result,
                canConfirm = result != null && result > 0,
            )
        }
    }
}

private fun Double.normalize(): String =
    if (this % 1.0 == 0.0 && abs(this) < 1e15) toLong().toString() else toString()

private sealed interface CalcToken {
    data class Num(val value: Double) : CalcToken
    data class Op(val symbol: Char) : CalcToken
    data object OpenParen : CalcToken
    data object CloseParen : CalcToken
}

internal fun evaluate(expression: String): Double? = runCatching {
    val tokens = tokenize(expression) ?: return null
    evalRpn(toRpn(tokens) ?: return null)
}.getOrNull()

private fun tokenize(expression: String): List<CalcToken>? {
    if (expression.isBlank()) return null
    val tokens = mutableListOf<CalcToken>()
    var index = 0
    while (index < expression.length) {
        val char = expression[index]
        when {
            char.isDigit() || char == '.' -> {
                var end = index + 1
                while (end < expression.length && (expression[end].isDigit() || expression[end] == '.')) end++
                val value = expression.substring(index, end).toDoubleOrNull() ?: return null
                tokens.add(CalcToken.Num(value))
                index = end
            }

            char in "+-*/" -> {
                tokens.add(CalcToken.Op(char))
                index++
            }

            char == '(' -> {
                tokens.add(CalcToken.OpenParen)
                index++
            }

            char == ')' -> {
                tokens.add(CalcToken.CloseParen)
                index++
            }

            char.isWhitespace() -> index++

            else -> return null
        }
    }
    return tokens.takeIf { it.isNotEmpty() }
}

private fun precedence(symbol: Char): Int = when (symbol) {
    'u' -> 3
    '*', '/' -> 2
    '+', '-' -> 1
    else -> 0
}

private fun toRpn(tokens: List<CalcToken>): List<CalcToken>? {
    val output = mutableListOf<CalcToken>()
    val operators = ArrayDeque<Char>()
    var expectOperand = true
    for (token in tokens) {
        when (token) {
            is CalcToken.Num -> {
                output.add(token)
                expectOperand = false
            }

            CalcToken.OpenParen -> {
                operators.addLast('(')
                expectOperand = true
            }

            CalcToken.CloseParen -> {
                while (operators.isNotEmpty() && operators.last() != '(') {
                    output.add(CalcToken.Op(operators.removeLast()))
                }
                if (operators.isEmpty() || operators.removeLast() != '(') return null
                expectOperand = false
            }

            is CalcToken.Op -> {
                val symbol = if (expectOperand) {
                    // ponytail: hanya unary minus yang didukung, unary lain ditolak
                    if (token.symbol != '-') return null
                    'u'
                } else token.symbol
                while (operators.isNotEmpty() && operators.last() != '(' &&
                    (precedence(operators.last()) > precedence(symbol) ||
                            (precedence(operators.last()) == precedence(symbol) && symbol != 'u'))
                ) {
                    output.add(CalcToken.Op(operators.removeLast()))
                }
                operators.addLast(symbol)
                expectOperand = true
            }
        }
    }
    while (operators.isNotEmpty()) {
        val symbol = operators.removeLast()
        if (symbol == '(') return null
        output.add(CalcToken.Op(symbol))
    }
    return output
}

private fun evalRpn(rpn: List<CalcToken>): Double {
    val stack = ArrayDeque<Double>()
    for (token in rpn) {
        when (token) {
            is CalcToken.Num -> stack.addLast(token.value)
            CalcToken.OpenParen, CalcToken.CloseParen -> throw IllegalArgumentException("paren in rpn")
            is CalcToken.Op -> {
                if (token.symbol == 'u') {
                    stack.addLast(-(stack.removeLastOrNull() ?: throw IllegalArgumentException("empty")))
                    continue
                }
                val right = stack.removeLastOrNull() ?: throw IllegalArgumentException("empty")
                val left = stack.removeLastOrNull() ?: throw IllegalArgumentException("empty")
                stack.addLast(
                    when (token.symbol) {
                        '+' -> left + right
                        '-' -> left - right
                        '*' -> left * right
                        '/' -> left / right
                        else -> throw IllegalArgumentException("op")
                    }
                )
            }
        }
    }
    if (stack.size != 1) throw IllegalArgumentException("stack")
    return stack.single()
}
