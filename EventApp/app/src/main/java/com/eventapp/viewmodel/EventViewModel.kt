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

    // Current page for pagination
    private var currentPage = 0
    private var isLoading = false

    /**
     * Fetch events with pagination
     */
    fun fetchEvents() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            eventRepository.fetchEvents(currentPage)
                .catch {
                    _eventsState.value = EventsUiState.Error(it.message ?: "Unknown error")
                    isLoading = false
                }
                .collect { events ->
                    _eventsState.value = when {
                        events.isEmpty() -> EventsUiState.Empty
                        currentPage == 0 -> EventsUiState.Success(events)
                        else -> EventsUiState.Append(events)
                    }
                    currentPage++
                    isLoading = false
                }
        }
    }

    /**
     * Toggle favorite status of an event
     */
    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            eventRepository.toggleFavorite(eventId).collect { isFavorited ->
                // Optionally update UI or show feedback
            }
        }
    }

    /**
     * Create a new event
     */
    fun createEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.createEvent(event)
                .catch {
                    // Handle error - show message to user
                }
                .collect { createdEvent ->
                    // Optionally update UI or show success message
                }
        }
    }

    /**
     * Fetch user's favorite events
     */
    fun fetchFavoriteEvents() {
        viewModelScope.launch {
            eventRepository.getFavoriteEvents().collect { favoriteEvents ->
                _favoriteEventsState.value = favoriteEvents
            }
        }
    }

    /**
     * Sealed class to represent different UI states for events
     */
    sealed class EventsUiState {
        object Loading : EventsUiState()
        object Empty : EventsUiState()
        data class Success(val events: List<Event>) : EventsUiState()
        data class Append(val events: List<Event>) : EventsUiState()
        data class Error(val message: String) : EventsUiState()
    }
}