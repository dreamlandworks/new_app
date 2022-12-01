package com.satrango.ui.user.user_dashboard.user_home_screen.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserKeywordsResModel(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable