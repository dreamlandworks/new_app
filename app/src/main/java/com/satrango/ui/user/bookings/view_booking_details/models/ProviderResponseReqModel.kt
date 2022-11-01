package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderResponseReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("status_id")
    val status_id: Int,
    @SerializedName("users_id")
    val users_id: Int
): Serializable