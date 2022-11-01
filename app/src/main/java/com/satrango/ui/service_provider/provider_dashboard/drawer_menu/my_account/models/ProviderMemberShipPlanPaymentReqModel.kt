package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderMemberShipPlanPaymentReqModel(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("period")
    val period: Int,
    @SerializedName("plan_id")
    val plan_id: Int,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("w_amount")
    val w_amount: String,
    @SerializedName("order_id")
    val order_id: String
): Serializable