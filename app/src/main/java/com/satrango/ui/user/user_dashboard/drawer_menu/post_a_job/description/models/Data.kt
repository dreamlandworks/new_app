package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("bid_range_id")
    val bid_range_id: String,
    @SerializedName("created_dts")
    val created_dts: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("range_slots")
    val range_slots: String
)