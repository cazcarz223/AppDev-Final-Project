package com.eventapp.network

import com.eventapp.model.Event
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for event-related network operations
 * TODO: Backend team should implement these endpoints
 */
interface EventApi {
    /**
     * Fetch paginated list of events
     * @param page Page number for pagination
     * @param pageSize Number of events per page
     * @return List of events
     */
    @GET("api/events/")
    suspend fun getEvents(): List<Event>

    /**
     * Toggle favorite status for an event
     * @param eventId Unique identifier of the event
     * @return Boolean indicating new favorite status
     */
    @POST("events/{eventId}/favorite")
    suspend fun toggleFavorite(@Path("eventId") eventId: String): Boolean


    /**
     * Create a new event
     * @param event Event details to be created
     * @return Created event with assigned ID
     */
    @POST("api/events/")
    suspend fun createEvent(@Body event: Event): Event

    @DELETE("api/events/{eventId}/")
    suspend fun deleteEvent(@Path("eventId") eventId: Int)

    @GET("api/events/{eventId}/")
    suspend fun getEventById(@Path("eventId") eventId: Int): Event

    @POST("api/events/{eventId}/add_user/")
    suspend fun addUserToEvent(
        @Path("eventId") eventId: Int,
        @Body request: AddUserRequest
    )

    /**
     * Purchase a ticket for an event
     * @param eventId Unique identifier of the event
     * @param quantity Number of tickets to purchase
     * @return Purchase confirmation details
     */
    @POST("events/{eventId}/purchase")
    suspend fun purchaseTicket(
        @Path("eventId") eventId: String,
        @Query("quantity") quantity: Int
    ): TicketPurchaseResponse

    /**
     * Search events based on various criteria
     * @param query Search query
     * @param location Optional location filter
     * @param dateFrom Optional start date filter
     * @param dateTo Optional end date filter
     * @return List of matching events
     */
    @GET("events/search")
    suspend fun searchEvents(
        @Query("query") query: String?,
        @Query("location") location: String? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): List<Event>
}

/**
 * Data class to represent ticket purchase response
 * TODO: Backend team to define exact structure
 */
data class AddUserRequest(
    val User_id: String,
    val Type: String
)

data class TicketPurchaseResponse(
    val purchaseId: String,
    val eventId: String,
    val quantity: Int,
    val totalPrice: Double,
    val purchaseDate: String,
    val qrCode: String? = null
)

data class UserDetails(
    val username: String,
    val name: String?,
    val email: String? = null,
    val additionalDetails: Map<String, Any>? = null
)
/**
 * Sealed class to represent payment methods
 */
sealed class PaymentMethod {
    data class CreditCard(
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    ) : PaymentMethod()

    data class PayPal(
        val email: String
    ) : PaymentMethod()

    data class ApplePay(
        val token: String
    ) : PaymentMethod()
}