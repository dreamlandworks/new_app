package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProviderGoalsInstallmentsListResModel(
    @SerializedName("goals_installments_details")
    val goals_installments_details: List<GoalsInstallmentsDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)