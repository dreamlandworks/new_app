package com.satrango.ui.user.bookings.cancel_booking.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserBookingCancelReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("cancelled_by")
    val cancelled_by: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("reasons")
    val reasons: String,
    @SerializedName("status_id")
    val status_id: Int
): Serializable