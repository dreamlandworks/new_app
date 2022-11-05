package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SpDetails(
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("qualification")
    val qualification: String,
    @SerializedName("sp_det_id")
    val sp_det_id: String
): Serializable