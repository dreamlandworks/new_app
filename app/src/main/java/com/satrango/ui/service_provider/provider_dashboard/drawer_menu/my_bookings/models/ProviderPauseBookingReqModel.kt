package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderPauseBookingReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("paused_at")
    val paused_at: String,
    @SerializedName("sp_id")
    val sp_id: Int
): Serializable