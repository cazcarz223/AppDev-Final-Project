package com.eventapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Data class representing an Event in the application
 * @param id Unique identifier for the event
 * @param name Name of the event
 * @param description Detailed description of the event
 * @param dateTime Date and time of the event
 * @param price Ticket price (can be 0 for free events)
 * @param location Location of the event
 * @param organizerId ID of the user who created the event
 * @param availableTickets Number of tickets still available
 * @param isFavorite Flag to indicate if the event is favorited by the current user
 */

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val dateTime: LocalDateTime,
    val price: Double,
    val location: String,
    val organizerId: String,
    val availableTickets: Int,
    val isFavorite: Boolean = false
)
