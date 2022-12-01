package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import java.io.Serializable

@Keep
data class ProviderPostBidReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("bid_type")
    val bid_type: Int,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("estimate_time")
    val estimate_time: Int,
    @SerializedName("estimate_type_id")
    val estimate_type_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("proposal")
    val proposal: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("post_title")
    val post_title: String
): Serializable