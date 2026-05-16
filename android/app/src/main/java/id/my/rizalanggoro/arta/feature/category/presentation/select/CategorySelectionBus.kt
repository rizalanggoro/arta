package id.my.rizalanggoro.arta.feature.category.presentation.select

import id.my.rizalanggoro.arta.domain.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object CategorySelectionBus {
    private val _selectedCategory = MutableSharedFlow<Category>(extraBufferCapacity = 1)
    val selectedCategory: SharedFlow<Category> = _selectedCategory.asSharedFlow()

    suspend fun emit(category: Category) {
        _selectedCategory.emit(category)
    }
}