package com.satrango.ui.service_provider.provider_dashboard.dashboard.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderDashboardResModel(
    @SerializedName("bids_awarded")
    val bids_awarded: String,
    @SerializedName("bookings_completed")
    val bookings_completed: String,
    @SerializedName("competitor_name")
    val competitor_name: String,
    @SerializedName("competitor_rank")
    val competitor_rank: String,
    @SerializedName("earnings")
    val earnings: String,
    @SerializedName("commission")
    val commission: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("sp_points")
    val sp_points: String,
    @SerializedName("sp_rank")
    val sp_rank: String,
    @SerializedName("sp_rating")
    val sp_rating: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total_bids")
    val total_bids: String,
    @SerializedName("total_bookings")
    val total_bookings: String
): Serializable