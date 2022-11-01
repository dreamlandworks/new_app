package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import java.io.Serializable

@Keep
data class ProviderBidEditReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("bid_det_id")
    val bid_det_id: Int,
    @SerializedName("bid_type")
    val bid_type: Int,
    @SerializedName("estimate_time")
    val estimate_time: Int,
    @SerializedName("estimate_type_id")
    val estimate_type_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("proposal")
    val proposal: String
): Serializable