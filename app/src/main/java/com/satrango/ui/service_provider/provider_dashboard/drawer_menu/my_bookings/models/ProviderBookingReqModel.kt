package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderBookingReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int
): Serializable