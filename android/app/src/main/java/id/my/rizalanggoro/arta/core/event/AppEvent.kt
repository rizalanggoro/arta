package id.my.rizalanggoro.arta.core.event

import id.my.rizalanggoro.arta.domain.Category

sealed class AppEvent {
    data object TransactionChanged : AppEvent()
    data class CategorySelected(val category: Category) : AppEvent()
}