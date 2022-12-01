package com.satrango.ui.service_provider.provider_dashboard.alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UpdateAlertsToReadReqModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("user_type")
    val user_type: String,
    @SerializedName("last_alert_id")
    val last_alert_id: String
): Serializable