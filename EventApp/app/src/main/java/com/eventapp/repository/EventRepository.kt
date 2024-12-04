package com.eventapp.repository

import com.eventapp.model.Event
import com.eventapp.network.EventApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository class for handling Event-related operations
 * Responsible for fetching events from network or local database
 *
 * @param eventApi Retrofit API interface for network calls
 * @param eventDao Room DAO for local database operations
 */
class EventRepository @Inject constructor(
    private val eventApi: EventApi
) {
    /**
     * Fetch events with pagination
     * @param page Current page number for infinite scrolling
     * @param pageSize Number of events to load per request
     */
    suspend fun fetchEvents(page: Int, pageSize: Int = 20): Flow<List<Event>> = flow {
        try {
            // TODO: Backend team - implement pagination endpoint
            val events = eventApi.getEvents(page, pageSize)

            // Cache events in local database
            eventDao.insertAll(events)

            emit(events)
        } catch (e: Exception) {
            // Fallback to local database if network fails
            val cachedEvents = eventDao.getPaginatedEvents(page, pageSize)
            emit(cachedEvents)
        }
    }

    /**
     * Toggle favorite status for an event
     * @param eventId ID of the event to toggle
     */
    suspend fun toggleFavorite(eventId: String): Flow<Boolean> = flow {
        try {
            // TODO: Backend team - create an endpoint to toggle event favorite
            val isFavorited = eventApi.toggleFavorite(eventId)

            // Update local database
            eventDao.updateFavoriteStatus(eventId, isFavorited)

            emit(isFavorited)
        } catch (e: Exception) {
            // Handle error - potentially show error to user
            emit(false)
        }
    }

    /**
     * Create a new event
     * @param event Event object to be created
     */
    suspend fun createEvent(event: Event): Flow<Event> = flow {
        try {
            // TODO: Backend team - create endpoint for event creation
            val createdEvent = eventApi.createEvent(event)

            // Insert into local database
            eventDao.insert(createdEvent)

            emit(createdEvent)
        } catch (e: Exception) {
            // Handle event creation error
        }
    }

    /**
     * Get user's favorite events
     */
    fun getFavoriteEvents(): Flow<List<Event>> {
        return eventDao.getFavoriteEvents()
    }
}