package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProviderMemberShipPlanPaymentResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("transaction_id")
    val transaction_id: Int
)