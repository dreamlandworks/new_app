package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderPostRequestInstallmentReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("inst_id")
    val inst_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("users_id")
    val users_id: Int
): Serializable