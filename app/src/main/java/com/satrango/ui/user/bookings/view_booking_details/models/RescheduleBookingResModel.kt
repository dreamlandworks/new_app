package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class RescheduleBookingResModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("reschedule_id")
    val reschedule_id: Int,
    @SerializedName("status")
    val status: Int
): Serializable