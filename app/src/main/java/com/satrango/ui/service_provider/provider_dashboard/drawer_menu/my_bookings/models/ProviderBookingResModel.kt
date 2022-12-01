package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProviderBookingResModel(
    @SerializedName("booking_details")
    val booking_details: List<BookingDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)