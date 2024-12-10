package com.eventapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.model.Event
import com.eventapp.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing Events-related UI state and interactions
 */
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    // State for events list
    private val _eventsState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val eventsState: StateFlow<EventsUiState> = _eventsState.asStateFlow()

    // State for favorite events
    private val _favoriteEventsState = MutableStateFlow<List<Event>>(emptyList())
    val favoriteEventsState: StateFlow<List<Event>> = _favoriteEventsState.asStateFlow()


    fun fetchEvents() {
        viewModelScope.launch {
            eventRepository.fetchEvents()
                .catch { e -> _eventsState.value = EventsUiState.Error(e.message ?: "Unknown error") }
                .collect { events ->
                    _eventsState.value = if (events.isEmpty()) {
                        EventsUiState.Empty
                    } else {
                        EventsUiState.Success(events)
                    }
                }
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            try {
                val currentEvents = (_eventsState.value as? EventsUiState.Success)?.events ?: return@launch
                val updatedEvents = currentEvents.map { event ->
                    if (event.id == eventId) event.copy(isFavorite = !event.isFavorite) else event
                }
                _eventsState.value = EventsUiState.Success(updatedEvents)
            } catch (e: Exception) {
                _eventsState.value = EventsUiState.Error("Failed to toggle favorite")
            }
        }
    }


    /**
     * Create a new event
     */
    fun createEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.createEvent(event)
                .catch { _eventsState.value = EventsUiState.Error("Failed to create event") }
                .collect { createdEvent ->
                    val currentEvents = (_eventsState.value as? EventsUiState.Success)?.events ?: emptyList()
                    _eventsState.value = EventsUiState.Success(currentEvents + createdEvent)
                }
        }
    }

    /**
     * Fetch user's favorite events
     */
    fun fetchFavoriteEvents() {

    }

    fun searchEvents(query: String) {
        // Placeholder function for search functionality
    }


    fun createNewEvent() {
        // Placeholder function for creating a new event
    }

    fun purchaseTicket(eventId: String) {
        // Placeholder function for purchasing a ticket
    }

    fun shareEvent(eventId: String) {
        // Placeholder function for sharing an event
    }

    init {
        fetchEvents()
        fetchFavoriteEvents()
    }

    sealed class EventsUiState {
        object Loading : EventsUiState()
        object Empty : EventsUiState()
        data class Success(val events: List<Event>) : EventsUiState()
        data class Error(val message: String) : EventsUiState()
        data class Append(val events: List<Event>) : EventsUiState()
    }
}