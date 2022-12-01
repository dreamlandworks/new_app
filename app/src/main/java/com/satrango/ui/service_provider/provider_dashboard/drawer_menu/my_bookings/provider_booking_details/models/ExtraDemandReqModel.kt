package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ExtraDemandReqModel(
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("material_advance")
    val material_advance: String,
    @SerializedName("technician_charges")
    val technician_charges: String,
    @SerializedName("key")
    val key: String
): Serializable