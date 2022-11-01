package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ComplaintReqModel(
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("module_id")
    val module_id: Int,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("user_type")
    val user_type: String
): Serializable