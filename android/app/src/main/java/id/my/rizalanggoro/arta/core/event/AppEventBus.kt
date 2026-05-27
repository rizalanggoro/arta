package id.my.rizalanggoro.arta.core.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object AppEventBus {
    private val _event = MutableSharedFlow<AppEvent>()
    val event = _event.asSharedFlow()

    suspend fun emit(event: AppEvent) = _event.emit(event)

    private val _updateEvent = MutableStateFlow(false)
    val updateEvent = _updateEvent.asStateFlow()

    suspend fun emitUpdate() = _updateEvent.emit(true)
}