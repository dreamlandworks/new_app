package com.satrango.ui.service_provider.provider_dashboard.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderPlanTxnReqModel(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int
): Serializable