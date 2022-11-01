package com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class FeedbackReqModel(
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("user_type")
    val user_type: String
): Serializable