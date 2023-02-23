package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingStatusDetail(
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("booking_status_id")
    val booking_status_id: String,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("status_id")
    val status_id: String
): Serializable