package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.place_bid.models

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment

data class ProviderPostBidReqModel(
    val amount: String,
    val attachments: List<Attachment>,
    val bid_type: Int,
    val booking_id: Int,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val key: String,
    val post_job_id: Int,
    val proposal: String,
    val sp_id: Int
)