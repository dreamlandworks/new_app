package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserProfileReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("city")
    val city: String
)
