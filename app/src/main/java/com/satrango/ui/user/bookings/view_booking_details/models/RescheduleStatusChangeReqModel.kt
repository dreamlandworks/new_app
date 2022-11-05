package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class RescheduleStatusChangeReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("reschedule_id")
    val reschedule_id: Int,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("status_id")
    val status_id: Int,
    @SerializedName("user_type")
    val user_type: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable