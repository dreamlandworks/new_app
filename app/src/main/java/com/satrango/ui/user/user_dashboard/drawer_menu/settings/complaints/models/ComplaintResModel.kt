package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ComplaintResModel(
    @SerializedName("complaint_id")
    val complaint_id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable