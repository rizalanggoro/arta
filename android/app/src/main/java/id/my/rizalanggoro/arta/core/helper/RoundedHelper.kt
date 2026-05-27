package id.my.rizalanggoro.arta.core.helper

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getTopRadius(index: Int, size: Int): Dp = when (index) {
    0 -> 16.dp
    else -> 4.dp
}

fun getBottomRadius(index: Int, size: Int): Dp = when (index) {
    size - 1 -> 16.dp
    else -> 4.dp
}