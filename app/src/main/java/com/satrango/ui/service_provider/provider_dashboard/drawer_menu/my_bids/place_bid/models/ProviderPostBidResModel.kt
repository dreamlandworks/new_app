package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderPostBidResModel(
    @SerializedName("bid_det_id")
    val bid_det_id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("status")
    val status: Int
): Serializable