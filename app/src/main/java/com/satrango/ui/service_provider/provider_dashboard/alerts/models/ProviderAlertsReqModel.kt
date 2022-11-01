package com.satrango.ui.service_provider.provider_dashboard.alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderAlertsReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("id")
    val id: String
): Serializable