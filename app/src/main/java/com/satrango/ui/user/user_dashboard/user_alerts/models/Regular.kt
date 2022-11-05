package com.satrango.ui.user.user_dashboard.user_alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Regular(
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type_id")
    val type_id: String,
    @SerializedName("updated_on")
    val updated_on: String,
    @SerializedName("user_id")
    val user_id: String
): Serializable