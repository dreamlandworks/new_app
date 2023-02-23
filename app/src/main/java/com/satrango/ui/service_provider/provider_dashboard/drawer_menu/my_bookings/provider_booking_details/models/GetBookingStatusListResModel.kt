package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetBookingStatusListResModel(
    @SerializedName("booking_status_details")
    val booking_status_details: List<BookingStatusDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable