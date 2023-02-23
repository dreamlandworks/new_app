package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MyBookingsResModel(
    @SerializedName("booking_details")
    val booking_details: List<BookingDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)