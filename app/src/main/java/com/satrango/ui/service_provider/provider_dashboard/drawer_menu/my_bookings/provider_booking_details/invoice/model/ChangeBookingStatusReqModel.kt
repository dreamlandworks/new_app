package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ChangeBookingStatusReqModel(
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("status_id")
    val status_id: String
): Serializable