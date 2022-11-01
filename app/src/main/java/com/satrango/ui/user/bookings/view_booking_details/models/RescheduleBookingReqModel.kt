package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class RescheduleBookingReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("rescheduled_date")
    val rescheduled_date: String,
    @SerializedName("rescheduled_time_slot_from")
    val rescheduled_time_slot_from: String,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("scheduled_time_slot_id")
    val scheduled_time_slot_id: Int,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("user_type")
    val user_type: String
): Serializable