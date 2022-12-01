package com.satrango.ui.user.bookings.raise_ticket.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class RaiseTicketReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("module_id")
    val module_id: Int,
    @SerializedName("user_type")
    val user_type: Int,
    @SerializedName("users_id")
    val users_id: String
): Serializable