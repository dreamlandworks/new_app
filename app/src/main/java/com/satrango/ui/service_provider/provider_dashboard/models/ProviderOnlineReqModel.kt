package com.satrango.ui.service_provider.provider_dashboard.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProviderOnlineReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("online_status_id")
    val online_status_id: Int,
    @SerializedName("sp_id")
    val sp_id: String
)