package com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class LeaderboardResModel(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("sp_data")
    val sp_data: SpData,
    @SerializedName("status")
    val status: Int
): Serializable