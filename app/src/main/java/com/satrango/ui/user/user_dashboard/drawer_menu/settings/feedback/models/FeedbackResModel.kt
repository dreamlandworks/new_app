package com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class FeedbackResModel(
    @SerializedName("feedback_id")
    val feedback_id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable