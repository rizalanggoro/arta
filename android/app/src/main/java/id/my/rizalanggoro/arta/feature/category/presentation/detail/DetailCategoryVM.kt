package id.my.rizalanggoro.arta.feature.category.presentation.detail

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute

@HiltViewModel(assistedFactory = DetailCategoryVM.Factory::class)
class DetailCategoryVM @AssistedInject constructor(
    @Assisted private val navKey: CategoryRoute.Detail,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: CategoryRoute.Detail): DetailCategoryVM
    }
}