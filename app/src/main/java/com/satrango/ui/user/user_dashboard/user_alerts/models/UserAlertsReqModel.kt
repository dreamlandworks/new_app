package com.satrango.ui.user.user_dashboard.user_alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserAlertsReqModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String
): Serializable