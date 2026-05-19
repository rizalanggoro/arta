package id.my.rizalanggoro.arta.core.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppEventBus {
    private val _event = MutableSharedFlow<AppEvent>()
    val event = _event.asSharedFlow()

    suspend fun emit(event: AppEvent) = _event.emit(event)
}