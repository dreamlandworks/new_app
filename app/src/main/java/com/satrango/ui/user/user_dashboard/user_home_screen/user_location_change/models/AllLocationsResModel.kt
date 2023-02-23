package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AllLocationsResModel(
    @SerializedName("data")
    val data: List<DataX>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable