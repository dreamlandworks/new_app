package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("id")
    val id: String,
    @SerializedName("module_name")
    val module_name: String
): Serializable