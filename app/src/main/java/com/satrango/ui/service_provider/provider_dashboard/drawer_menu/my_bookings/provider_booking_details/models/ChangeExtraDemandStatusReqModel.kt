package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ChangeExtraDemandStatusReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("status_id")
    val status_id: Int
): Serializable