package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderInvoiceReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("extra_demand")
    val extra_demand: String // 0 - Not Raised, 1 - Raised
): Serializable