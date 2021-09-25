package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.place_bid.models

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment

data class ProviderBidEditReqModel(
    val amount: String,
    val attachments: List<Attachment>,
    val bid_det_id: Int,
    val bid_type: Int,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val key: String,
    val proposal: String
)