package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderDeleteBidAttachmentReqModel(
    @SerializedName("bid_attach_id")
    val bid_attach_id: Int,
    @SerializedName("key")
    val key: String
): Serializable