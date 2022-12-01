package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingDetailsReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("category_id")
    val category_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable