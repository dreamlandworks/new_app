package com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("fname")
    val fname: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("profession")
    val profession: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("points_count")
    val points_count: String,
    @SerializedName("rank")
    val rank: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("total_people")
    val total_people: String,
): Serializable