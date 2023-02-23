package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserBidRangesResModel(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)