package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ExpenditureIncurredReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("expenditure_incurred")
    val expenditure_incurred: Int,
    @SerializedName("key")
    val key: String
): Serializable