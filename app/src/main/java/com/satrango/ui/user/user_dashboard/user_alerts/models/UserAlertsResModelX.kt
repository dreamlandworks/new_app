package com.satrango.ui.user.user_dashboard.user_alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserAlertsResModelX(
    @SerializedName("action")
    val action: List<Action>,
    @SerializedName("message")
    val message: String,
    @SerializedName("regular")
    val regular: List<Regular>,
    @SerializedName("status")
    val status: Int
): Serializable